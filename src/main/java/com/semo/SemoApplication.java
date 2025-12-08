package com.semo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SemoApplication.class, args);
    System.out.println("http://localhost:8080");
	}

}
