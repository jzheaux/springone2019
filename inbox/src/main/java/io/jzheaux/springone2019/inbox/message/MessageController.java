package io.jzheaux.springone2019.inbox.message;

import java.util.UUID;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;

/**
 * @author Rob Winch
 */
@Controller
@RequestMapping("/messages")
public class MessageController {
	private final MessageService messages;

	public MessageController(MessageService messages) {
		this.messages = messages;
	}

	@GetMapping("/inbox")
	Rendering inbox() {
		return Rendering.view("messages/inbox")
				.modelAttribute("messages", this.messages.inbox())
				.build();
	}

	@GetMapping("/{id}")
	Rendering message(@PathVariable UUID id) {
		return Rendering.view("messages/view")
				.modelAttribute("message", this.messages.findById(id))
				.build();
	}

	@PostMapping
	Mono<String> send(Message message) {
		return this.messages
			.send(message)
			.thenReturn("redirect:/messages/inbox");
	}
}
