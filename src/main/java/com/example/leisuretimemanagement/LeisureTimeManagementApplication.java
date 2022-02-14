package com.example.leisuretimemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;

@EnableAsync
@SpringBootApplication
public class LeisureTimeManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeisureTimeManagementApplication.class, args);
	}

	@Bean
	Validator validator(){ return new LocalValidatorFactoryBean();}
}
