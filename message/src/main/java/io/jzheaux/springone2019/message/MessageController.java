package io.jzheaux.springone2019.message;

import java.util.Optional;
import java.util.UUID;

import io.jzheaux.springone2019.message.tenant.TenantResolver;

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
		return this.messages.findByTenant(TenantResolver.resolve());
	}

	@GetMapping("/inbox")
	Iterable<Message> inbox(@CurrentUserId String currentUserId) {
		return this.messages.findByToAndTenant(UUID.fromString(currentUserId), TenantResolver.resolve());
	}

	@GetMapping("/{id}")
	Optional<Message> read(@PathVariable UUID id) {
		return this.messages.findByIdAndTenant(id, TenantResolver.resolve());
	}

	@PostMapping
	Message send(@CurrentUserId String from, Message message) {
		if (message.getId() == null) {
			message.setId(UUID.randomUUID());
		}
		message.setFrom(UUID.fromString(from));
		message.setTenant(TenantResolver.resolve());
		return this.messages.save(message);
	}
}
