package com.haru.LogMe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //jpa auditing 활성화
@SpringBootApplication
public class LogMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogMeApplication.class, args);
	}

}
