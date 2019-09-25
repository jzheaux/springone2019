package io.jzheaux.springone2019.message;

import java.util.Optional;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
	Iterable<Message> all(@RequestHeader("tenant") String tenant) {
		return this.messages.findByTenant(tenant);
	}

	@GetMapping("/inbox")
	Iterable<Message> inbox(@CurrentUserId String currentUserId, @RequestHeader("tenant") String tenant) {
		return this.messages.findByToAndTenant(UUID.fromString(currentUserId), tenant);
	}

	@GetMapping("/{id}")
	Optional<Message> read(@PathVariable UUID id, @RequestHeader("tenant") String tenant) {
		return this.messages.findByIdAndTenant(id, tenant);
	}

	@PostMapping
	Message send(@CurrentUserId String from, @RequestHeader("tenant") String tenant, Message message) {
		if (message.getId() == null) {
			message.setId(UUID.randomUUID());
		}
		message.setFrom(UUID.fromString(from));
		message.setTenant(tenant);
		return this.messages.save(message);
	}
}
