package io.jzheaux.springone2019.inbox;

import io.jzheaux.springone2019.inbox.security.JwtService;
import io.jzheaux.springone2019.inbox.security.SignedJwtExchangeFilterFunction;
import io.jzheaux.springone2019.inbox.tenant.TenantFilterChain;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
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

	@Autowired JwtService jwtService;

	@Bean
	WebClient auth(SignedJwtExchangeFilterFunction jwt) {
		return WebClient.builder().filter(jwt).build();
	}

	@Bean
	WebClientReactiveAuthorizationCodeTokenResponseClient authorizationCode(WebClient auth) {
		WebClientReactiveAuthorizationCodeTokenResponseClient authorizationCode =
				new WebClientReactiveAuthorizationCodeTokenResponseClient();
		authorizationCode.setWebClient(auth);
		return authorizationCode;
	}

	@Bean
	WebClientReactiveRefreshTokenTokenResponseClient refreshToken(WebClient auth) {
		WebClientReactiveRefreshTokenTokenResponseClient refreshToken =
				new WebClientReactiveRefreshTokenTokenResponseClient();
		refreshToken.setWebClient(auth);
		return refreshToken;
	}

	@Bean
	ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider
			(WebClientReactiveRefreshTokenTokenResponseClient refreshToken) {
		return ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
				.authorizationCode()
				.refreshToken(r -> r.accessTokenResponseClient(refreshToken)).build();
	}

	@Bean
	WebClient rest(ReactiveClientRegistrationRepository clients,
				   ServerOAuth2AuthorizedClientRepository authz,
				   ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider) {

		DefaultReactiveOAuth2AuthorizedClientManager manager =
				new DefaultReactiveOAuth2AuthorizedClientManager(clients, authz);
		manager.setAuthorizedClientProvider(authorizedClientProvider);
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
				new ServerOAuth2AuthorizedClientExchangeFilterFunction(manager);
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
