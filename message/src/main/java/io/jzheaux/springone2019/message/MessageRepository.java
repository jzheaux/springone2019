package io.jzheaux.springone2019.message;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/**
 * @author Rob Winch
 */
public interface MessageRepository extends CrudRepository<Message, UUID>, QueryByExampleExecutor<Message> {
}
