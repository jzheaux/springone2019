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

		UUID robId = UUID.fromString("5bfce384-9e2e-4ace-a28d-3df2f662cb65");
		UUID joeId = UUID.fromString("3e745d37-1f4f-4523-8408-a8c212aa7439");
		UUID joshId = UUID.fromString("3d05248f-ea11-4b8c-9ad0-0a472e698e9e");

		UUID filipId = UUID.fromString("a2b2c791-e05a-4934-be9b-fb488f87700a");
		UUID riaId = UUID.fromString("3df7633b-0375-4609-a256-93bab5d19762");

		this.users.deleteAll().block();

		this.users.save(new User(robId, "rob@example.com", password, "Rob", "Winch")).block();
		this.users.save(new User(joeId, "joe@example.com", password, "Joe", "Grandja")).block();
		this.users.save(new User(joshId, "josh@example.com", password, "Josh", "Cummings")).block();
		this.users.save(new User(filipId, "filip@example.com", password, "Filip", "Hanik")).block();
		this.users.save(new User(riaId, "ria@example.com", password, "Ria", "Stein")).block();
	}
}
