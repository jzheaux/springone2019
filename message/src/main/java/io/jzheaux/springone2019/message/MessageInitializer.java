package io.jzheaux.springone2019.message;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.kafka.annotation.KafkaListener;
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
		UUID robId = UUID.fromString("5bfce384-9e2e-4ace-a28d-3df2f662cb65");
		UUID joeId = UUID.fromString("3e745d37-1f4f-4523-8408-a8c212aa7439");
		UUID joshId = UUID.fromString("3d05248f-ea11-4b8c-9ad0-0a472e698e9e");
		UUID filipId = UUID.fromString("a2b2c791-e05a-4934-be9b-fb488f87700a");

		String one = "one";
		String two = "two";

		withTenant(one);
		this.messages.deleteAll();
		this.messages.save(new Message(robId, joeId, "Hello World", one));
		this.messages.save(new Message(robId, joeId, "Greetings Spring Enthusiasts", one));
		this.messages.save(new Message(joeId, robId, "Hola", one));
		this.messages.save(new Message(joeId, robId, "Hey Java Devs", one));
		this.messages.save(new Message(robId, joshId, "Aloha", one));
		this.messages.save(new Message(joshId, robId, "Welcome to Spring", one));

		withTenant(two);
		this.messages.deleteAll();
		this.messages.save(new Message(joshId, filipId, "SAML is the bomb", two));
		this.messages.save(new Message(filipId, joshId, "no doubt", two));
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

	@KafkaListener(topics = "clients")
	public void action(Map<String, Map<String, Object>> payload) {
		UUID joeId = UUID.fromString("94d835cc-c70f-47c1-8206-2ad7c8a37565");
		UUID joshId = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");
		if (payload.containsKey("created")) {
			Map<String, Object> client = payload.get("created");
			String tenant = (String) client.get("tenantAlias");
			withTenant(tenant);
			this.messages.save(new Message(joshId, joeId, "Hey, nice job adding multi-tenancy, man!", tenant));
		}
	}
}
