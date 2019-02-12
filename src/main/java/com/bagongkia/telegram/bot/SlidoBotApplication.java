package com.bagongkia.telegram.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class SlidoBotApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(SlidoBotApplication.class, args);
	}
}