package io.sbs.service;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

import io.sbs.dto.AuthenticationProfileDTO;
import io.sbs.dto.UserDTO;
import io.sbs.exception.BusinessException;
import io.sbs.exception.ValidationException;
import io.sbs.model.Account;
import io.sbs.model.User;

@Service
public class UserServiceImpl implements UserService {
	
	MongoClient mongoClient = MongoClients.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	MongoDatabase database = mongoClient.getDatabase("mydb");

	@Autowired
	private MongoTemplate mongoTemplate;

	private static ApplicationContext applicationContext;
	
	@Override
	public List<Account> getUserAccountDetails(String userid) {

	    MongoCollection<Document> collection = database.getCollection("user");
	    Document myDoc = collection.find(eq("userid", userid)).first();
	    
	    MongoCollection<Document> collection_acc = database.getCollection("account");
	    List<Document> cursor_accounts = collection_acc.find(eq("userid", userid)).into(new ArrayList<Document>());
	      
		List<Account> acc_list = new ArrayList<Account>();
	    for (Document account : cursor_accounts) {
	    	Account a = new Account();
	    	a.setAcc_holder_name(myDoc.get("name").toString());
	        a.setAccount_number(Double.parseDouble(account.get("account_num").toString()));
	        a.setAcc_type(account.get("type").toString());
	        a.setAcc_balance(Double.parseDouble(account.get("balance").toString()));
	        a.setUser_id(userid);
	        acc_list.add(a);
	    }

		return acc_list;		
	}

	@Override
	public User getUserInfo(String userid) {
		MongoCollection<Document> collection = database.getCollection("user");
		Document myDoc = collection.find(eq("userid", userid)).first();
		User user = new User();
		user.setName(myDoc.get("name").toString());
		user.setEmailString(myDoc.get("email").toString());
		user.setAddress(myDoc.get("address").toString());
		return user;
	}
	
	@Override
	public void register(UserDTO userDTO) {
		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("uid").is(userDTO.getUid())), UserDTO.class, "user");
		if (dto != null) {
			throw new ValidationException("the user already exists");
		}
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
		userDTO.setPassword(hashedPassword);
		mongoTemplate.save(userDTO, "user");
		AuthenticationProfileDTO authenticationProfileDTO = new AuthenticationProfileDTO();
		authenticationProfileDTO.setPassword(hashedPassword);
		authenticationProfileDTO.setUid(userDTO.getUid());
		authenticationProfileDTO.setUsername(userDTO.getUsername());
		mongoTemplate.save(authenticationProfileDTO, "authenticationProfile");
	}

	
	@Override
	public UserDTO login(UserDTO userDTO) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("username").is(userDTO.getUsername())), UserDTO.class, "user");
		if (dto == null) {
			throw new BusinessException("the account doesn't register！");
		}
		if (!passwordEncoder.matches(userDTO.getPassword(), dto.getPassword())) {
			throw new BusinessException("password is wrong！");
		}
//		EmailService es = new EmailService();
//		if(!es.send_mail(dto.getEmailString())) {
//			throw new BusinessException("Error in sending the email！");
//		}
		dto.setPassword(null);
		return dto;
	}
	
	@Override
	public UserDTO updateDetails(String userId, UserDTO user) {

		Update update = new Update();
		if(user.getAddress()!=null) {
			update.set("address", user.getAddress());
		}
		if(user.getEmailString()!=null) {
			update.set("emailString", user.getEmailString());
		}
		
		UpdateResult userObj = mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userId)), update, User.class, "user");
		if (userObj == null) {
			throw new BusinessException("cannot be updated！");
		}
		return user;
	}

}
