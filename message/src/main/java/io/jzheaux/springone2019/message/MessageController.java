package io.jzheaux.springone2019.message;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rob Winch
 */
@RestController
@RequestMapping("/messages")
public class MessageController {
	private final MessageRepository messages;

	public MessageController(MessageRepository messages) {
		this.messages = messages;
	}

	@GetMapping("/list")
	Iterable<Message> all() {
		return this.messages.findAll();
	}

	@GetMapping("/inbox")
	Iterable<Message> inbox(@CurrentUserId String currentUserId) {
		Message example = new Message();
		example.setTo(UUID.fromString(currentUserId));
		return this.messages.findAll(Example.of(example));
	}

	@GetMapping("/{id}")
	Optional<Message> read(@PathVariable UUID id) {
		return this.messages.findById(id);
	}

	@PostMapping
	Message send(@CurrentUserId String from, Message message) {
		if (message.getId() == null) {
			message.setId(UUID.randomUUID());
		}
		message.setFrom(UUID.fromString(from));
		return this.messages.save(message);
	}
}
