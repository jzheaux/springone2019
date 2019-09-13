package io.jzheaux.springone2019.tenant;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class TenantApplication {

	@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers;

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		return props;
	}

	@Bean
	public ProducerFactory<String, Map<String, ?>> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, Map<String, ?>> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public SecurityWebFilterChain springSecurity(ServerHttpSecurity http) {
		// @formatter:off
		http
			.authorizeExchange()
				.anyExchange().permitAll()
				.and()
			.oauth2Client()
				.and()
			.csrf().disable();
		// @formatter:on

		return http.build();
	}

	@Bean
	public WebClient rest(ReactiveClientRegistrationRepository clients,
		ServerOAuth2AuthorizedClientRepository authz) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
				new ServerOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);
		oauth2.setDefaultClientRegistrationId("keycloak");

		return WebClient.builder()
				.filter(oauth2).build();
	}

	public static void main(String[] args) {
		SpringApplication.run(TenantApplication.class, args);
	}
}
