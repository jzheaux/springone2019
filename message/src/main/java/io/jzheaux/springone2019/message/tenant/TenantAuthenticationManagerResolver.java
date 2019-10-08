package io.jzheaux.springone2019.message.tenant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import com.nimbusds.jwt.JWTParser;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

/**
 * @author Josh Cummings
 */
@Component
public class TenantAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {
	private final BearerTokenResolver resolver = new DefaultBearerTokenResolver();
	private final Map<String, String> tenants = new HashMap<>();

	private final Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

	public TenantAuthenticationManagerResolver() {
		this.tenants.put("one", "http://idp:9999/auth/realms/one");
		this.tenants.put("two", "http://idp:9999/auth/realms/two");
	}

	@Override
	public AuthenticationManager resolve(HttpServletRequest request) {
		return this.authenticationManagers.computeIfAbsent(toTenant(request), this::fromTenant);
	}

	private String toTenant(HttpServletRequest request) {
		try {
			String token = this.resolver.resolve(request);
			return (String) JWTParser.parse(token).getJWTClaimsSet().getClaim("tenant_id");
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private AuthenticationManager fromTenant(String tenant) {
		return Optional.ofNullable(this.tenants.get(tenant))
				.map(JwtDecoders::fromIssuerLocation)
				.map(JwtAuthenticationProvider::new)
				.orElseThrow(() -> new IllegalArgumentException("unknown tenant"))::authenticate;
	}
}
