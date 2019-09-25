package io.jzheaux.springone2019.message;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @author Rob Winch
 */
@Document
public class Message {
	@Id
	private UUID id;

	private UUID to;

	private UUID from;

	private String text;

	private String tenant;

	public Message() { }

	public Message(UUID to, UUID from, String text, String tenant) {
		this.id = UUID.randomUUID();
		this.to = to;
		this.from = from;
		this.text = text;
		this.tenant = tenant;
	}

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

	public String getTenant() {
		return this.tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
}
