package io.jzheaux.springone2019.message;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Component;

import static java.time.Instant.now;

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

		String one = "one";
		String two = "two";
		String three = "three";

		withTenant(one);
		this.messages.save(new Message(robId, joeId, "Hello World", one));
		this.messages.save(new Message(robId, joeId, "Greetings Spring Enthusiasts", one));
		this.messages.save(new Message(joeId, robId, "Hola", one));
		this.messages.save(new Message(joeId, robId, "Hey Java Devs", one));
		this.messages.save(new Message(robId, joshId, "Aloha", one));
		this.messages.save(new Message(joshId, robId, "Welcome to Spring", one));

		withTenant(two);
		this.messages.save(new Message(joshId, filipId, "SAML is the bomb", two));
		this.messages.save(new Message(filipId, joshId, "no doubt", two));

		withTenant(three);
		this.messages.save(new Message(joshId, riaId, "Hey, nice job adding multi-tenancy, man!", three));
	}

	private void withTenant(String tenant) {
		OAuth2AuthenticatedPrincipal principal = new DefaultOAuth2AuthenticatedPrincipal
				(Collections.singletonMap("tenant_id", tenant), Collections.emptyList());
		OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", now(), now().plusSeconds(1));
		BearerTokenAuthentication authentication =
				new BearerTokenAuthentication(principal, token, Collections.emptyList());
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);
	}
}
