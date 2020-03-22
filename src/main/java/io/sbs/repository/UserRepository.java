package io.sbs.repository;
import io.sbs.model.ApplicationUser;

import org.springframework.data.mongodb.repository.MongoRepository;



public interface UserRepository extends MongoRepository<ApplicationUser,String> {

//	public String getAll() {
//		return "from DAO Users layer, Hello World";
//	}

	ApplicationUser findByUsername(String username);
}
