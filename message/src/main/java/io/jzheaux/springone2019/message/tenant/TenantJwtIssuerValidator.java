package io.jzheaux.springone2019.message.tenant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.stereotype.Component;

@Component
public class TenantJwtIssuerValidator implements OAuth2TokenValidator<Jwt> {
	private final Map<String, String> tenants = new HashMap<>();
	private final Map<String, JwtIssuerValidator> validators = new HashMap<>();

	public TenantJwtIssuerValidator() {
		this.tenants.put("one", "http://idp:9999/auth/realms/one");
		this.tenants.put("two", "http://idp:9999/auth/realms/two");
	}

	@Override
	public OAuth2TokenValidatorResult validate(Jwt token) {
		return this.validators.computeIfAbsent(toTenant(token), this::fromTenant)
				.validate(token);
	}

	private String toTenant(Jwt jwt) {
		return jwt.getClaim("tenant_id");
	}

	private JwtIssuerValidator fromTenant(String tenant) {
		return Optional.ofNullable(this.tenants.get(tenant))
				.map(JwtIssuerValidator::new)
				.orElseThrow(() -> new IllegalArgumentException("unknown tenant"));
	}
}
