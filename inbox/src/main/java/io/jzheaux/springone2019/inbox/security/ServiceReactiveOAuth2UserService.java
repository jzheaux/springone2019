package io.jzheaux.springone2019.inbox.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import io.jzheaux.springone2019.inbox.user.User;
import io.jzheaux.springone2019.inbox.user.UserService;

import java.util.Collection;
import java.util.Map;

/**
 * @author Rob Winch
 */
@Component
public class ServiceReactiveOAuth2UserService
		implements ReactiveOAuth2UserService<OidcUserRequest, OidcUser> {

	private final OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();

	private final UserService users;

	public ServiceReactiveOAuth2UserService(UserService users) {
		this.users = users;
	}

	@Override
	public Mono<OidcUser> loadUser(OidcUserRequest oidcUserRequest)
			throws OAuth2AuthenticationException {
		return this.delegate.loadUser(oidcUserRequest).flatMap(this::create);
	}

	private Mono<OidcUser> create(OidcUser oidcUser) {
		return this.users.findByEmail(oidcUser.getEmail())
				.map(u -> new CustomOidcUser(u, oidcUser));
	}

	private class CustomOidcUser extends User implements OidcUser {
		private OidcUser oidcUser;

		public CustomOidcUser(User u, OidcUser oidcUser) {
			super(u);
			this.oidcUser = oidcUser;
		}

		public Map<String, Object> getClaims() {
			return oidcUser.getClaims();
		}

		public OidcUserInfo getUserInfo() {
			return oidcUser.getUserInfo();
		}

		public OidcIdToken getIdToken() {
			return oidcUser.getIdToken();
		}

		public Collection<? extends GrantedAuthority> getAuthorities() {
			return oidcUser.getAuthorities();
		}

		public Map<String, Object> getAttributes() {
			return oidcUser.getAttributes();
		}

		public String getName() {
			return oidcUser.getName();
		}
	}
}
