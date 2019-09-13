package io.jzheaux.springone2019.inbox.user;

import java.util.UUID;

import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 */
public interface UserService {
	Mono<User> save(User user);
	Mono<User> findByEmail(String email);
	Mono<User> findById(UUID id);
}
