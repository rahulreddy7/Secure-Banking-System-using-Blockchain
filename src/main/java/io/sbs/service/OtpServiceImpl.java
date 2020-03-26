package io.sbs.service;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Collections.emptyList;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.sbs.dto.UserDTO;
import io.sbs.model.LoginOTP;
import io.sbs.repository.OtpRepository;

@Service
public class OtpServiceImpl implements UserDetailsService{
	
	@Autowired
	private OtpRepository otpRepository;
	
	final MongoClient mongoClient = MongoClients
			.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	final MongoDatabase database = mongoClient.getDatabase("mydb");
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Query query = new Query(Criteria.where("username"));
		LoginOTP dto = mongoTemplate.findOne(
				Query.query(Criteria.where("username").is(username)),
				LoginOTP.class, "loginOTP");
		if (dto == null) {
			throw new UsernameNotFoundException(username);
		}
		return new User(dto.getUsername(), dto.getOtp(), emptyList());
	}
	
}
