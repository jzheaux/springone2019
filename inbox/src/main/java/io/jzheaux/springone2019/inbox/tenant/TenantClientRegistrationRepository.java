package io.jzheaux.springone2019.inbox.tenant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import reactor.core.publisher.Mono;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;

@Component
public class TenantClientRegistrationRepository implements ReactiveClientRegistrationRepository {
	private final Map<String, String> tenants = new HashMap<>();
	private final Map<String, Mono<ClientRegistration>> clients = new HashMap<>();

	public TenantClientRegistrationRepository() {
		this.tenants.put("one", "http://idp:9999/auth/realms/one");
		this.tenants.put("two", "http://idp:9999/auth/realms/two");
	}

	@Override
	public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
		return this.clients.computeIfAbsent(registrationId, this::fromTenant);
	}

	private Mono<ClientRegistration> fromTenant(String registrationId) {
		return Optional.ofNullable(this.tenants.get(registrationId))
				.map(uri -> Mono.defer(() -> clientRegistration(uri, registrationId)).cache())
				.orElse(Mono.error(new IllegalArgumentException("unknown tenant")));
	}

	private Mono<ClientRegistration> clientRegistration(String uri, String registrationId) {
		return Mono.just(ClientRegistrations.fromIssuerLocation(uri)
				.registrationId(registrationId)
				.clientId("message")
				.clientSecret("bfbd9f62-02ce-4638-a370-80d45514bd0a")
				.userNameAttributeName("email")
				.scope("openid", "message:read")
				.build());
	}
}
