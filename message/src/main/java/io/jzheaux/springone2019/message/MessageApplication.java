package io.jzheaux.springone2019.message;

import java.util.function.Supplier;

import io.jzheaux.springone2019.message.tenant.TenantResolver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MessageApplication {

	@Bean
	Supplier<String> tenant() {
		return TenantResolver::resolve;
	}

	public static void main(String[] args) {
		SpringApplication.run(MessageApplication.class, args);
	}

}
