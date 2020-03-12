package io.sbs.repository;

import io.sbs.model.Account;

import org.springframework.data.mongodb.repository.MongoRepository;
public interface AccountRepository extends MongoRepository<Account,String>{
	
//	public String getdata() {
//		return ("ALL OK");
//	}

}
