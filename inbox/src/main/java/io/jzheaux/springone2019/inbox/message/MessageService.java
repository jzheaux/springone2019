package io.jzheaux.springone2019.inbox.message;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 */
public interface MessageService {
	Flux<Message> inbox();
	Mono<Message> findById(UUID id);
	Mono<Message> send(Message message);
}
