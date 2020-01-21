package io.jzheaux.springone2019.tenant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Josh Cummings
 */
@RestController
@RequestMapping(path="/tenants")
public class TenantController {
	private final KafkaTemplate<String, Map<String, ?>> kafka;
	private final WebClient rest;
	private final String keycloakUri;
	private final String keycloakApiUri;

	@Autowired
	public TenantController(KafkaTemplate<String, Map<String, ?>> kafka, WebClient rest, @Value("${keycloakUri}") String keycloakUri) {
		this.kafka = kafka;
		this.rest = rest;
		this.keycloakUri = keycloakUri;
		this.keycloakApiUri = keycloakUri + "/admin/realms";
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	Flux<Tenant> tenants() {
		return this.rest.get()
				.uri(this.keycloakUri + "/admin/realms")
				.retrieve()
				.bodyToFlux(Map.class)
				.map(this::toTenant);
	}

	@GetMapping(path="/{alias}", produces = MediaType.APPLICATION_JSON_VALUE)
	Mono<Tenant> findByAlias(@PathVariable String alias) {
		return this.rest.get()
				.uri(this.keycloakUri + "/admin/realms/" + alias)
				.retrieve()
				.bodyToMono(Map.class)
				.map(this::toTenant);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	Mono<Tenant> add(@RequestParam(defaultValue = "false") boolean useJwt, @RequestBody Tenant tenant) {
		Map<String, Object> body = toTenant(tenant.getAlias());
		Map<String, Object> scope = new HashMap<>();
		scope.put("name", "message:read");
		scope.put("protocol", "openid-connect");
		Map<String, Object> client = toClient(tenant.getAlias());
		if (useJwt) {
			client.put("clientAuthenticatorType", "client-jwt");
		}
		Iterable<Map<String, Object>> users = Arrays.asList(
				toUser(tenant.getAlias(), "6fb5a754-c666-4859-9452-f885796ee73e", "rob", "Rob", "Winch", "rob@example.com"),
				toUser(tenant.getAlias(), "94d835cc-c70f-47c1-8206-2ad7c8a37565", "joe", "Joe", "Grandja", "joe@example.com"),
				toUser(tenant.getAlias(), "219168d2-1da4-4f8a-85d8-95b4377af3c1", "josh", "Josh", "Cummings", "josh@example.com"));

		return create("/", body)
				.checkpoint()
				.flatMap(this::ifCreated)
				.flatMap(response -> produce("tenants", toTenant(body)))
				.flatMap(response -> create("/" + tenant.getAlias() + "/client-scopes", scope))
				.flatMap(this::ifCreated)
				.flatMap(created -> create("/" + tenant.getAlias() + "/clients", client))
				.checkpoint()
				.flatMap(this::ifCreated)
				.flatMap(response -> produce("clients", toClient(client, tenant.getAlias())))
				.flatMap(created -> createList("/" + tenant.getAlias() + "/users", users))
				.checkpoint()
				.flatMap(this::ifCreated)
				.map(created -> toTenant(body));
	}

	@DeleteMapping("/{alias}")
	public Mono<ResponseEntity<?>> delete(@PathVariable String alias) {
		return this.rest.delete()
				.uri(this.keycloakApiUri + "/" + alias)
				.exchange()
				.map(response -> ResponseEntity.noContent().build());
	}

	private Mono<List<ClientResponse>> createList(String uri, Iterable<? extends Object> bodies) {
		return Flux.fromIterable(bodies)
				.flatMap(body -> create(uri, body))
				.collectList();
	}

	private Mono<ClientResponse> create(String uri, Object body) {
		return this.rest.post()
				.uri(this.keycloakApiUri + uri)
				.bodyValue(body)
				.exchange();
	}

	private Mono<ClientResponse> ifCreated(List<ClientResponse> response) {
		return ifCreated(response.get(0));
	}

	private Mono<ClientResponse> ifCreated(ClientResponse response) {
		return response.bodyToMono(Map.class)
				.switchIfEmpty(Mono.just(Collections.emptyMap()))
				.flatMap(map -> Mono.just(Collections.singletonMap(response.statusCode(), map))
						.filter(r -> response.statusCode() == HttpStatus.CREATED)
						.switchIfEmpty(Mono.error(() -> new IllegalArgumentException("failed to create: " + map)))
						.map(m -> response));
	}

	private <T> Mono<T> produce(String topic, T t) {
		return Mono.defer(() -> {
			Map<String, T> action = Collections.singletonMap("created", t);
			this.kafka.send(topic, action);
			return Mono.just(action.get("created"));
		});
	}

	private Tenant toTenant(Map<String, Object> response) {
		String alias = (String) response.get("realm");
		Tenant tenant = new Tenant();
		tenant.setAlias(alias);
		tenant.setIssuerUri(this.keycloakUri + "/realms/" + alias);
		tenant.setJwkSetUri(this.keycloakUri + "/realms/" + alias + "/protocol/openid-connect/certs");
		return tenant;
	}

	private Map<String, Object> toTenant(String alias) {
		Map<String, Object> map = new HashMap<>();
		map.put("realm", alias);
		map.put("enabled", true);
		return map;
	}

	private Client toClient(Map<String, Object> response, String alias) {
		Client client = new Client();
		client.setClientId((String) response.get("clientId"));
		client.setClientSecret((String) response.get("secret"));
		client.setIssuerUri(this.keycloakUri + "/realms/" + alias);
		client.setTenantAlias(alias);
		return client;
	}

	private Map<String, Object> toClient(String alias) {
		Map<String, Object> map = new HashMap<>();

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("use.jwks.url", true);
		attributes.put("jwks.url", "http://" + alias + ":8080/jwks");
		map.put("attributes", attributes);

		map.put("clientId", "message");
		map.put("secret", "bfbd9f62-02ce-4638-a370-80d45514bd0a");
		map.put("clientAuthenticatorType", "client-secret");
		map.put("enabled", true);
		map.put("bearerOnly", false);
		map.put("redirectUris", Arrays.asList("http://" + alias + ":8080/login/oauth2/code/" + alias, "http://" + alias + ":8080"));
		map.put("standardFlowEnabled", true);
		map.put("directAccessGrantsEnabled", true);
		map.put("publicClient", false);
		map.put("protocolMappers", Arrays.asList(protocolMapper("tenant_id"), protocolMapper("user_id")));
		map.put("defaultClientScopes", Arrays.asList("role_list", "profile", "email"));
		map.put("optionalClientScopes", Arrays.asList("address", "message:read", "phone", "offline_access", "admin"));

		return map;
	}

	private Map<String, Object> protocolMapper(String claimName) {
		Map<String, Object> tenantIdMapper = new HashMap<>();
		tenantIdMapper.put("name", claimName);
		tenantIdMapper.put("protocol", "openid-connect");
		tenantIdMapper.put("protocolMapper", "oidc-usermodel-attribute-mapper");
		tenantIdMapper.put("consentRequired", false);

		Map<String, Object> tenantIdConfig = new HashMap<>();
		tenantIdConfig.put("userinfo.token.claim", true);
		tenantIdConfig.put("user.attribute", claimName);
		tenantIdConfig.put("id.token.claim", true);
		tenantIdConfig.put("access.token.claim", true);
		tenantIdConfig.put("claim.name", claimName);
		tenantIdConfig.put("jsonType.label", "String");
		tenantIdMapper.put("config", tenantIdConfig);

		return tenantIdMapper;
	}

	private Map<String, Object> toUser(String alias, String id, String username, String firstName, String lastName, String email) {
		Map<String, Object> map = new HashMap<>();
		map.put("username", username);

		Map<String, Object> credentials = new HashMap<>();
		credentials.put("type", "password");
		credentials.put("value", "password");
		map.put("credentials", Collections.singletonList(credentials));

		map.put("enabled", true);
		map.put("emailVerified", true);
		map.put("firstName", firstName);
		map.put("lastName", lastName);
		map.put("email", email);

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("tenant_id", Collections.singletonList(alias));
		attributes.put("user_id", Collections.singletonList(id));
		map.put("attributes", attributes);

		return map;
	}
}
