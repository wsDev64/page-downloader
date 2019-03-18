package com.oracle.techtask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.oracle.techtask")
public class TechTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechTaskApplication.class, args);
	}

}
