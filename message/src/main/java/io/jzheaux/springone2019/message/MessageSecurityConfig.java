package io.jzheaux.springone2019.message;

import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MessageSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	JWTClaimsSetAwareJWSKeySelector<SecurityContext> jwsKeySelector;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeRequests(r -> r
				.antMatchers("/messages/list").hasAuthority("SCOPE_admin")
				.antMatchers(GET, "/messages/**").hasAuthority("SCOPE_message:read")
				.antMatchers(POST, "/messages/**").hasAuthority("SCOPE_message:write")
				.anyRequest().authenticated())
			.oauth2ResourceServer(o -> o.jwt());
		// @formatter:on
	}

	@Bean
	JwtDecoder jwtDecoder() {
		DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
		processor.setJWTClaimsSetAwareJWSKeySelector(this.jwsKeySelector);
		return new NimbusJwtDecoder(processor);
	}
}