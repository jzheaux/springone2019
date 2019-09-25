package io.jzheaux.springone2019.message;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Rob Winch
 */
public interface MessageRepository extends CrudRepository<Message, UUID> {
	Iterable<Message> findByTenant(String tenant);

	Iterable<Message> findByToAndTenant(UUID to, String tenant);

	Optional<Message> findByIdAndTenant(UUID id, String tenant);
}
