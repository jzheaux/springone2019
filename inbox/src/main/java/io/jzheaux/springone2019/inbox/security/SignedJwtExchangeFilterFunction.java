package io.jzheaux.springone2019.inbox.security;

import reactor.core.publisher.Mono;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

@Component
public class SignedJwtExchangeFilterFunction implements ExchangeFilterFunction {
	private final JwtService jwtService;

	public SignedJwtExchangeFilterFunction(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		BodyInserters.FormInserter<String> body =
				(BodyInserters.FormInserter<String>) request.body();

		return Mono.subscriberContext()
				.filter(c -> c.hasKey(ClientRegistration.class))
				.map(c -> c.get(ClientRegistration.class))
				.filter(clientRegistration -> "jwt".equals(clientRegistration.getClientAuthenticationMethod().getValue()))
				.map(this.jwtService::encode)
				.map(assertion -> ClientRequest.from(request)
						.body(body.with("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"))
						.body(body.with("client_assertion", assertion))
						.build())
				.defaultIfEmpty(request)
				.flatMap(next::exchange);
	}
}
