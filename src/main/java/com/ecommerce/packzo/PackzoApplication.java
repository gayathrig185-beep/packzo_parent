package com.ecommerce.packzo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PackzoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PackzoApplication.class, args);
	}

}
