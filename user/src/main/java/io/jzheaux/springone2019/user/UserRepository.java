package io.jzheaux.springone2019.user;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 */
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
	Mono<User> findByEmail(String email);

	Mono<User> findByAlias(String alias);
}
