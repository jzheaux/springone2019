package io.jzheaux.springone2019.inbox.user;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @author Rob Winch
 */
@Controller
@RequestMapping("/users/")
public class UserController {
	private final UserService users;

	public UserController(UserService users) {
		this.users = users;
	}

	@GetMapping("/signup")
	Mono<String> signupForm(@ModelAttribute User user) {
		return Mono.just("users/form");
	}

	@PostMapping("/signup")
	Mono<String> signup(@Valid User user, BindingResult result) {
		if(result.hasErrors()) {
			return signupForm(user);
		}
		return Mono.just(user)
				.flatMap(this.users::save)
				.then(Mono.just("redirect:/"));
	}
}
