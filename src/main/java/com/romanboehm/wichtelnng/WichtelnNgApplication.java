package com.romanboehm.wichtelnng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WichtelnNgApplication {

	public static void main(String[] args) {
		createSpringApplication().run(args);
	}

	public static SpringApplication createSpringApplication() {
        return new SpringApplication(WichtelnNgApplication.class);
    }

}
