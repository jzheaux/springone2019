package io.jzheaux.springone2019.inbox;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Rob Winch
 */
@Controller
public class IndexController {
	@GetMapping("/")
	String index() {
		return "redirect:/messages/inbox";
	}
}
