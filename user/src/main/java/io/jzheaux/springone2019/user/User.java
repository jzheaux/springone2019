package io.jzheaux.springone2019.user;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;

/**
 * @author Rob Winch
 */
@Document
public class User {
	@Id
	private UUID id;

	@Indexed
	@NotEmpty(message = "This field is required")
	private String email;

	private String password;

	@NotEmpty(message = "This field is required")
	private String firstName;

	@NotEmpty(message = "This field is required")
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
