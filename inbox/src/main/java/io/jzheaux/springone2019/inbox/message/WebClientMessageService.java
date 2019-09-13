package io.jzheaux.springone2019.inbox.message;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.jzheaux.springone2019.inbox.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.jzheaux.springone2019.inbox.user.UserService;

/**
 * @author Rob Winch
 */
@Component
public class WebClientMessageService implements MessageService {
	private final WebClient rest;

	private final String messagesUrl;

	private final UserService users;

	public WebClientMessageService(WebClient rest,
			@Value("${messages-url}") String messagesUrl, UserService users) {
		this.rest = rest;
		this.messagesUrl = messagesUrl;
		this.users = users;
	}

	@Override
	public Flux<Message> inbox() {
		return this.rest.get()
			.uri(this.messagesUrl + "/inbox")
			.retrieve()
			.bodyToFlux(MessageDto.class)
			.flatMap(this::toMessage);
	}

	@Override
	public Mono<Message> findById(UUID id) {
		return this.rest.get()
				.uri(this.messagesUrl + "/{id}", id)
				.retrieve()
				.bodyToMono(MessageDto.class)
				.flatMap(this::toMessage);
	}

	@Override
	public Mono<Message> send(Message message) {
		return this.rest.post()
			.uri(this.messagesUrl)
			.bodyValue(message)
			.exchange()
			.then(Mono.empty());
	}

	private Mono<Message> toMessage(MessageDto dto) {
		Mono<User> to = this.users.findById(dto.getTo());
		Mono<User> from = this.users.findById(dto.getFrom());
		return Mono.zip(to, from)
			.map(t2 -> new Message(dto.getId(), t2.getT1(), t2.getT2(), dto.getText()));
	}
}
