package io.jzheaux.springone2019.inbox.tenant;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.security.config.Customizer.withDefaults;

public class TenantFilterChain implements WebFilter, ApplicationContextAware {
	private final Map<String, Mono<WebFilter>> tenants = new HashMap<>();
	private final ReactiveClientRegistrationRepository clients;
	private ApplicationContext context;

	public TenantFilterChain(ReactiveClientRegistrationRepository clients) {
		this.clients = clients;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		Mono<ClientRegistration> tenant = toTenant(exchange);
		Mono<WebFilter> filter = tenant.flatMap(this::fromTenant);

		return Mono.zip(tenant, filter)
				.flatMap(tuple -> tuple.getT2().filter(exchange, chain)
						.subscriberContext(Context.of(ClientRegistration.class, tuple.getT1()))
						.thenReturn(exchange))
				.switchIfEmpty(chain.filter(exchange).thenReturn(exchange))
				.then(Mono.empty());
	}

	private Mono<ClientRegistration> toTenant(ServerWebExchange exchange) {
		String host = UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
				.build().getHost();
		return this.clients.findByRegistrationId(host);
	}

	private Mono<WebFilter> fromTenant(ClientRegistration registration) {
		return this.tenants.computeIfAbsent(registration.getRegistrationId(), tenant -> {
			ServerHttpSecurity http = new ContextAwareServerHttpSecurity(this.context);

			OidcClientInitiatedServerLogoutSuccessHandler handler =
					new OidcClientInitiatedServerLogoutSuccessHandler(this.clients);
			handler.setPostLogoutRedirectUri(URI.create("http://" + tenant + ":8080"));

			ServerAuthenticationEntryPoint entryPoint =
					new RedirectServerAuthenticationEntryPoint("/oauth2/authorization/" + tenant);

			// @formatter:off
			http
				.authorizeExchange(e -> e.anyExchange().authenticated())
				.logout(l -> l.logoutSuccessHandler(handler))
				.oauth2Login(withDefaults())
				.exceptionHandling(e -> e.authenticationEntryPoint(entryPoint));
			// @formatter:on

			return Mono.just((WebFilter) new WebFilterChainProxy(http.build())).cache();
		});
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	private static class ContextAwareServerHttpSecurity extends ServerHttpSecurity {
		ContextAwareServerHttpSecurity(ApplicationContext context) {
			super.setApplicationContext(context);
		}
	}
}
