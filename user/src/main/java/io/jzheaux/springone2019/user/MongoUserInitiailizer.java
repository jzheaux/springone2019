package io.jzheaux.springone2019.user;

import java.util.UUID;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * @author Rob Winch
 */
@Component
class MongoUserInitiailizer implements SmartInitializingSingleton {
	private final UserRepository users;

	MongoUserInitiailizer(UserRepository users) {
		this.users = users;
	}

	@Override
	public void afterSingletonsInstantiated() {
		// sha256 w/ salt encoded "password"
		String password = "73ac8218b92f7494366bf3a03c0c2ee2095d0c03a29cb34c95da327c7aa17173248af74d46ba2d4c";

		UUID robId = UUID.fromString("6fb5a754-c666-4859-9452-f885796ee73e");
		UUID joeId = UUID.fromString("94d835cc-c70f-47c1-8206-2ad7c8a37565");
		UUID joshId = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");

		UUID filipId = UUID.fromString("a2b2c791-e05a-4934-be9b-fb488f87700a");
		UUID riaId = UUID.fromString("3df7633b-0375-4609-a256-93bab5d19762");

		this.users.save(new User(robId, "rob@example.com", password, "Rob", "Winch")).block();
		this.users.save(new User(joeId, "joe@example.com", password, "Joe", "Grandja")).block();
		this.users.save(new User(joshId, "josh@example.com", password, "Josh", "Cummings")).block();
		this.users.save(new User(filipId, "filip@example.com", password, "Filip", "Hanik")).block();
		this.users.save(new User(riaId, "ria@example.com", password, "Ria", "Stein")).block();
	}
}
