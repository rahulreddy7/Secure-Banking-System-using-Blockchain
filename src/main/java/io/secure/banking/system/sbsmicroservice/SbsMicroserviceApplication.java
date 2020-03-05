package io.secure.banking.system.sbsmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import io.sbs.controller.AccountsController;

@SpringBootApplication
@ComponentScan(basePackageClasses = AccountsController.class)
public class SbsMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbsMicroserviceApplication.class, args);
	}

}
