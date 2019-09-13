package io.jzheaux.springone2019.inbox.message;

import java.util.UUID;

/**
 * @author Rob Winch
 */
class MessageDto {
	private UUID id;

	private UUID to;

	private UUID from;

	private String text;

	public MessageDto() {}

	public UUID getId() {
		return this.id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getTo() {
		return this.to;
	}

	public void setTo(UUID to) {
		this.to = to;
	}

	public UUID getFrom() {
		return this.from;
	}

	public void setFrom(UUID from) {
		this.from = from;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
