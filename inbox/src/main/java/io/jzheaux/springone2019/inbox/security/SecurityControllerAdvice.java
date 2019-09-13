package io.jzheaux.springone2019.inbox.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import io.jzheaux.springone2019.inbox.user.User;
import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 */
@ControllerAdvice
public class SecurityControllerAdvice {

	@ModelAttribute("currentUser")
	Mono<User> currentUser(@AuthenticationPrincipal Mono<User> currentUser) {
		return currentUser;
	}
}
