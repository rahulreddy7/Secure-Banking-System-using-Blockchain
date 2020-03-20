package io.secure.banking.system.sbsmicroservice;

import java.io.Serializable;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.SpringVersion;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ComponentScan({"io.sbs.*"})
@EnableMongoRepositories("io.sbs.*")
@EnableAutoConfiguration()
public class SbsMicroserviceApplication{
	
	public static void main(String[] args) {
		SpringApplication.run(SbsMicroserviceApplication.class, args);
	}

}
