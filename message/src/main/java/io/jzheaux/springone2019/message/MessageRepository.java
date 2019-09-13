package io.jzheaux.springone2019.message;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Rob Winch
 */
public interface MessageRepository extends CrudRepository<Message, UUID> {
	Iterable<Message> findByTo(UUID to);
}
