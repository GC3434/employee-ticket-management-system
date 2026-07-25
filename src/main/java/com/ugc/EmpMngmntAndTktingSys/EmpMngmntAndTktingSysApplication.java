package com.ugc.EmpMngmntAndTktingSys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EmpMngmntAndTktingSysApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmpMngmntAndTktingSysApplication.class, args);
	}

}
