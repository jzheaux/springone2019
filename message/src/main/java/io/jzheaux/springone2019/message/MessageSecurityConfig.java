package io.jzheaux.springone2019.message;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MessageSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeRequests(r -> r
				.antMatchers("/messages/list").hasAuthority("SCOPE_admin")
				.antMatchers(GET, "/messages/**").hasAuthority("SCOPE_message:read")
				.antMatchers(POST, "/messages/**").hasAuthority("SCOPE_message:write")
				.anyRequest().authenticated())
			.oauth2ResourceServer(o -> o.authenticationManagerResolver(this.authenticationManagerResolver));
		// @formatter:on
	}
}