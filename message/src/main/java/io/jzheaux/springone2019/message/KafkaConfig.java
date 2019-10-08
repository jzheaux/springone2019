package io.jzheaux.springone2019.message;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {
	@Value("${kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "message");
		return props;
	}

	@Bean
	public ConsumerFactory<String, Map<String, Map<String, Object>>> consumerFactory() {
		TypeReference<Map<String, Map<String, Object>>> type = new TypeReference<Map<String, Map<String, Object>>>() {};
		JsonDeserializer<Map<String, Map<String, Object>>> deser = new JsonDeserializer<>(type, false);
		return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), deser);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Map<String, Map<String, Object>>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Map<String, Map<String, Object>>> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());

		return factory;
	}
}
