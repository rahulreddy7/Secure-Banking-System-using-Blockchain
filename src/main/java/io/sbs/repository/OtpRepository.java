package io.sbs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.sbs.model.ApplicationUser;
import io.sbs.model.LoginOTP;

public interface OtpRepository extends MongoRepository<LoginOTP,String> {
	
	LoginOTP findByUsername(String username);
}
