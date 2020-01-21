package io.jzheaux.springone2019.inbox.tenant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import reactor.core.publisher.Mono;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;

@Component
public class TenantClientRegistrationRepository implements ReactiveClientRegistrationRepository {
	private final Map<String, String> tenants = new HashMap<>();
	private final Map<String, Mono<ClientRegistration>> clients = new HashMap<>();
	private final OAuth2ClientProperties oauth2Properties;

	public TenantClientRegistrationRepository(OAuth2ClientProperties oauth2Properties) {
		this.oauth2Properties = oauth2Properties;
		this.tenants.put("one", "http://idp:9999/auth/realms/one");
		this.tenants.put("two", "http://idp:9999/auth/realms/two");
	}

	@Override
	public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
		return this.clients.computeIfAbsent(registrationId, this::fromTenant);
	}

	private Mono<ClientRegistration> fromTenant(String registrationId) {
		return Optional.ofNullable(oauth2Properties.getProvider().get(registrationId))
				.map(provider -> {
					OAuth2ClientProperties.Registration registration = 
						oauth2Properties.getRegistration().get(registrationId);
					return Mono.defer(() -> clientRegistration(provider, 
						registration, registrationId)).cache();
				})
				.orElse(Mono.error(new IllegalArgumentException("unknown tenant")));
	}

	private Mono<ClientRegistration> clientRegistration(OAuth2ClientProperties.Provider provider, 
		OAuth2ClientProperties.Registration registration, String registrationId) {
		return Mono.just(ClientRegistrations.fromIssuerLocation(provider.getIssuerUri())
			.registrationId(registrationId)
			.clientId(registration.getClientId())
			.clientSecret(registration.getClientSecret())
			.clientAuthenticationMethod(ClientAuthenticationMethod.POST)
			.userNameAttributeName(provider.getUserNameAttribute())
			.scope(registration.getScope())
			.build());
	}

	private Mono<ClientRegistration> clientRegistration(String uri, String registrationId, String clientSecret) {
		return Mono.just(ClientRegistrations.fromIssuerLocation(uri)
				.registrationId(registrationId)
				.clientId("message")
				.clientSecret(clientSecret)
				.clientAuthenticationMethod(new ClientAuthenticationMethod("post"))
				.userNameAttributeName("email")
				.scope("openid", "message:read")
				.build());
	}

	@KafkaListener(topics = "tenants")
	public void action(Map<String, Map<String, Object>> action) {
		if (action.containsKey("created")) {
			Map<String, Object> tenant = action.get("created");
			String alias = (String) tenant.get("alias");
			String issuerUri = (String) tenant.get("issuerUri");
			this.tenants.put(alias, issuerUri);
			this.clients.remove(alias);
		}
	}

	@KafkaListener(topics = "clients")
	public void clients(Map<String, Map<String, Object>> payload) {
		if (payload.containsKey("created")) {
			Map<String, Object> client = payload.get("created");
			String issuerUri = (String) client.get("issuerUri");
			String registrationId = (String) client.get("tenantAlias");
			String clientSecret = (String) client.get("clientSecret");
			this.clients.put(registrationId, clientRegistration(issuerUri, registrationId, clientSecret));
		}
	}
}
