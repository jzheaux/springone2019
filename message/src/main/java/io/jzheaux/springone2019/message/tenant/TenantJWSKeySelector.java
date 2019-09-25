package io.jzheaux.springone2019.message.tenant;

import java.net.URL;
import java.security.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;

import org.springframework.stereotype.Component;

/**
 * @author Josh Cummings
 */
@Component
public class TenantJWSKeySelector implements JWTClaimsSetAwareJWSKeySelector<SecurityContext> {
	private final Map<String, String> tenants = new HashMap<>();
	private final Map<String, JWSKeySelector<SecurityContext>> selectors = new HashMap<>();

	public TenantJWSKeySelector() {
		this.tenants.put("one", "http://idp:9999/auth/realms/one/protocol/openid-connect/certs");
		this.tenants.put("two", "http://idp:9999/auth/realms/two/protocol/openid-connect/certs");
	}

	@Override
	public List<? extends Key> selectKeys(JWSHeader jwsHeader, JWTClaimsSet jwtClaimsSet, SecurityContext securityContext)
			throws KeySourceException {
		return this.selectors.computeIfAbsent(toTenant(jwtClaimsSet), this::fromTenant)
				.selectJWSKeys(jwsHeader, securityContext);
	}

	private String toTenant(JWTClaimsSet claimSet) {
		return (String) claimSet.getClaim("tenant_id");
	}

	private JWSKeySelector<SecurityContext> fromTenant(String tenant) {
		return Optional.ofNullable(this.tenants.get(tenant))
				.map(this::fromUri)
				.orElseThrow(() -> new IllegalArgumentException("unknown tenant"));
	}

	private JWSKeySelector<SecurityContext> fromUri(String uri) {
		try {
			return JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(new URL(uri));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
}
