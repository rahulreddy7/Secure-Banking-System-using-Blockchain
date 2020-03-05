package io.secure.banking.system.sbsmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import io.sbs.controller.AccountsController;

@SpringBootApplication
@ComponentScan({"io.sbs.*"})
@EnableMongoRepositories("io.sbs.*")
public class SbsMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbsMicroserviceApplication.class, args);
	}

}
