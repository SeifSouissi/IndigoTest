package com.indigo.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@ComponentScan("com.indigo.event")
public class EventApiApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(EventApiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EventApiApplication.class, args);
		logger.info(" Event API Application STARTED. ");
		System.out.println(" Event API Application STARTED. ");
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}
