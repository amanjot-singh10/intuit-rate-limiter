package com.intuit.ratelimiter;

import com.intuit.ratelimiter.configurations.RateProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties({RateProperties.class})
public class RatelimiterApplication  implements ApplicationRunner {

	@Autowired
	RateProperties rateProperties;

	public static void main(String[] args) {
;
		ConfigurableApplicationContext appContext = SpringApplication.run(RatelimiterApplication.class, args);

	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("amanaman "+rateProperties);
	}
}
