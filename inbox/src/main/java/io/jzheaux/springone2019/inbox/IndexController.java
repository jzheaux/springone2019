package io.jzheaux.springone2019.inbox;

import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 * @author Julius Krah
 */
@Controller
public class IndexController {
	@GetMapping("/")
	String index() {
		return "redirect:/messages/inbox";
	}

	@PostMapping("/tenant")
	Mono<String> tenant (ServerWebExchange exchange) {
		Mono<MultiValueMap<String, String>> tenant = exchange.getFormData();
		return tenant.map(map -> {
			System.out.println(map.get("tenant").get(0));
			return map;
		})
		.thenReturn("redirect:/messages/inbox");
	}
}
