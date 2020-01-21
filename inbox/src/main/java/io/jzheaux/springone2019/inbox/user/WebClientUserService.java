package io.jzheaux.springone2019.inbox.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 */
@Component
public class WebClientUserService implements UserService {
	private final WebClient rest;

	private final String usersUrl;

	public WebClientUserService(WebClient rest,
			@Value("${users-url}") String usersUrl) {
		this.rest = rest;
		this.usersUrl = usersUrl;
	}

	public Mono<User> save(User user) {
		return this.rest.post()
				.uri(this.usersUrl)
				.bodyValue(user)
				.retrieve()
				.bodyToMono(User.class);
	}

	public Mono<User> findByEmail(String email) {
		return this.rest.get()
				.uri(this.usersUrl + "/?email={email}", email)
				.retrieve()
				.bodyToMono(User.class);
	}

	public Mono<User> findById(UUID id) {
		return this.rest.get()
				.uri(this.usersUrl + "/{id}", id)
				.retrieve()
				.bodyToMono(User.class);
	}
}
