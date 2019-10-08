package io.jzheaux.springone2019.inbox.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwkSetController {
	private final JwtService jwtService;

	public JwkSetController(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@GetMapping("/jwks")
	public String jwks() {
		return this.jwtService.jwkSet();
	}
}
