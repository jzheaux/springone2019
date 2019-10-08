package io.jzheaux.springone2019.message;

import java.util.function.Supplier;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import io.jzheaux.springone2019.message.tenant.TenantMongoDbFactory;
import io.jzheaux.springone2019.message.tenant.TenantResolver;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;

@SpringBootApplication
public class MessageApplication {

	@Bean
	Supplier<String> tenant() {
		return TenantResolver::resolve;
	}

	@Bean
	MongoDbFactory mongoDbFactory(MongoClient mongo) {
		return new TenantMongoDbFactory(mongo, "test");
	}

	@Bean
	public MongoClient mongo(MongoProperties properties, ObjectProvider<MongoClientOptions> options, Environment environment) {
		return (new MongoClientFactory(properties, environment)).createMongoClient((MongoClientOptions) options.getIfAvailable());
	}

	public static void main(String[] args) {
		SpringApplication.run(MessageApplication.class, args);
	}

}
