package io.jzheaux.springone2019.message;

import java.util.UUID;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * @author Rob Winch
 */
@Component
class MessageInitializer implements SmartInitializingSingleton {
	private final MessageRepository messages;

	MessageInitializer(MessageRepository messages) {
		this.messages = messages;
	}

	@Override
	public void afterSingletonsInstantiated() {
		UUID robId = UUID.fromString("6fb5a754-c666-4859-9452-f885796ee73e");
		UUID joeId = UUID.fromString("94d835cc-c70f-47c1-8206-2ad7c8a37565");
		UUID joshId = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");
		UUID filipId = UUID.fromString("a2b2c791-e05a-4934-be9b-fb488f87700a");
		UUID riaId = UUID.fromString("3df7633b-0375-4609-a256-93bab5d19762");

		this.messages.save(new Message(robId, joeId, "Hello World"));
		this.messages.save(new Message(robId, joeId, "Greetings Spring Enthusiasts"));
		this.messages.save(new Message(joeId, robId, "Hola"));
		this.messages.save(new Message(joeId, robId, "Hey Java Devs"));
		this.messages.save(new Message(robId, joshId, "Aloha"));
		this.messages.save(new Message(joshId, robId, "Welcome to Spring"));

		this.messages.save(new Message(joshId, filipId, "SAML is the bomb"));
		this.messages.save(new Message(filipId, joshId, "no doubt"));

		this.messages.save(new Message(joshId, riaId, "Hey, nice job adding multi-tenancy, man!"));
	}
}
