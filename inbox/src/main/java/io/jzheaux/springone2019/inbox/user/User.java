package io.jzheaux.springone2019.inbox.user;

import java.util.UUID;

/**
 * @author Rob Winch
 */
public class User {
	private UUID id;

	private String email;

	private String password;

	private String firstName;

	private String lastName;

	private String alias;

	public User() {}

	public User(User user) {
		this(user.getId(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
		this.alias = user.getAlias();
	}

	public User(UUID id, String email, String password, String firstName,
			String lastName) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public UUID getId() {
		return this.id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
