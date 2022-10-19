package com.hyundai.crawling;

import com.hyundai.crawling.controller.MainController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class CrawlingApplication {

	@Autowired
	private MainController mainController;

	public static void main(String[] args) {
		SpringApplication.run(CrawlingApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(ApplicationContext ctx) {
		return args -> System.out.println(mainController.fast());
	}
}
