package io.jzheaux.springone2019.inbox.security;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
	private final LinkedHashMap<String, RSAKey> keys = new LinkedHashMap<>();

	public JwtService() {
		try {
			RSAKey key = new RSAKeyGenerator(2048)
					.keyID(UUID.randomUUID().toString())
					.keyUse(KeyUse.SIGNATURE)
					.generate();
			this.keys.put(key.getKeyID(), key);
		} catch (JOSEException e) {
			throw new IllegalArgumentException(e);
		}
	}

	String encode(ClientRegistration client) {
		Map.Entry<String, RSAKey> entry = this.keys.entrySet().iterator().next();
		JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
				.keyID(entry.getKey())
				.build();
		Payload payload = new Payload(new JWTClaimsSet.Builder()
				.issuer("http://" + client.getRegistrationId() + ":8080")
				.subject(client.getClientId())
				.audience((String) client.getProviderDetails().getConfigurationMetadata().get("issuer"))
				.jwtID(UUID.randomUUID().toString())
				.expirationTime(new Date(Instant.now().plusSeconds(3600).toEpochMilli()))
				.build().toJSONObject());
		JWSObject jws = new JWSObject(header, payload);
		try {
			RSASSASigner signer = new RSASSASigner(entry.getValue());
			jws.sign(signer);
			return jws.serialize();
		} catch (JOSEException e) {
			throw new IllegalStateException(e);
		}
	}

	String jwkSet() {
		List<JWK> jwks = this.keys.values().stream()
				.map(RSAKey::toPublicJWK)
				.collect(Collectors.toList());
		return new JWKSet(jwks).toString();
	}
}
