package io.jzheaux.springone2019.inbox;

import io.jzheaux.springone2019.inbox.tenant.TenantFilterChain;
import reactor.core.publisher.Mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebFilter;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult.match;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult.notMatch;

@SpringBootApplication
public class InboxApplication {

	@Bean
	WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations,
			ServerOAuth2AuthorizedClientRepository authorizedClients) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
				new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);
		oauth2.setDefaultOAuth2AuthorizedClient(true);
		return WebClient.builder()
			.filter(oauth2)
			.build();
	}

	@Order(-101)
	WebFilter tenant(ReactiveClientRegistrationRepository clients) {
		return new TenantFilterChain(clients);
	}

	@Bean
	SecurityWebFilterChain notenant(ServerHttpSecurity http) {
		// @formatter:on
		http
			.securityMatcher(noregistration())
			.authorizeExchange(e -> e.anyExchange().denyAll())
			.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));
		// @formatter:off

		return http.build();
	}

	private ServerWebExchangeMatcher noregistration() {
		return exchange ->
				Mono.subscriberContext()
						.flatMap(ctx -> ctx.hasKey(ClientRegistration.class) ? notMatch() : match());
	}

	public static void main(String[] args) {
		SpringApplication.run(InboxApplication.class, args);
	}
}
