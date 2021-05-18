package cst438hw2;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuration class that helps create a message exchange

@Configuration
public class ConfigPublisher {
	@Bean
	public FanoutExchange fanout() {
		return new FanoutExchange("city-reservation");
	}
}
