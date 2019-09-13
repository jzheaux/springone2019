package io.jzheaux.springone2019.inbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.config.Customizer.withDefaults;

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

	@Bean
	SecurityWebFilterChain notenant(ServerHttpSecurity http) {
		// @formatter:off
		http
			.authorizeExchange(e -> e.anyExchange().authenticated())
			.oauth2Login(withDefaults());
		// @formatter:on

		return http.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(InboxApplication.class, args);
	}
}
