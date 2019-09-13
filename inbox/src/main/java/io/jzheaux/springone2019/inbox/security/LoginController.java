package io.jzheaux.springone2019.inbox.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Rob Winch
 */
@Controller
public class LoginController {
	@GetMapping("/login")
	public String login() {
		return "login";
	}
}
