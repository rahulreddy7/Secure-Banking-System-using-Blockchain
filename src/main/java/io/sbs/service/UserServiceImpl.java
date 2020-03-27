package io.sbs.service;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.apache.tomcat.util.json.JSONParser;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;

import io.sbs.dto.UserDTO;
import io.sbs.dto.WorkflowDTO;
import io.sbs.exception.BusinessException;
import io.sbs.model.Account;
import io.sbs.model.User;
import net.minidev.json.JSONObject;
import io.sbs.constant.UserType;
import io.sbs.dto.AuthenticationProfileDTO;
import io.sbs.dto.UserDTO;
import io.sbs.exception.BusinessException;
import io.sbs.exception.ValidationException;
import io.sbs.model.Account;
import io.sbs.model.ApplicationUser;

@Service
public class UserServiceImpl implements UserService {
	
	MongoClient mongoClient = MongoClients.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	MongoDatabase database = mongoClient.getDatabase("mydb");


	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private Environment env;
	private static ApplicationContext applicationContext;
	
	@Override
	public List<Account> getUserAccountDetails(String username) {

	    MongoCollection<Document> collection = database.getCollection("user");
	    Document myDoc = collection.find(eq("username", username)).first();
	    
	    MongoCollection<Document> collection_acc = database.getCollection("account");
	    List<Document> cursor_accounts = collection_acc.find(eq("username", username)).into(new ArrayList<Document>());
	      
		List<Account> acc_list = new ArrayList<Account>();
	    for (Document account : cursor_accounts) {
	    	Account a = new Account();
	    	a.setAcc_holder_name(myDoc.get("name").toString());
	        a.setAccount_number(Double.parseDouble(account.get("account_num").toString()));
	        a.setAcc_type(account.get("type").toString());
	        a.setAcc_balance(Double.parseDouble(account.get("balance").toString()));
	        a.setUsername(username);
	        acc_list.add(a);
	    }

		return acc_list;		
	}

	@Override
	public ApplicationUser getUserInfo(String username) {
		MongoCollection<Document> collection = database.getCollection("user");
		Document myDoc = collection.find(eq("username", username)).first();
		ApplicationUser user = new ApplicationUser();
		user.setName(myDoc.get("name").toString());
		user.setEmailString(myDoc.get("email").toString());
		user.setAddress(myDoc.get("address").toString());
		return user;
	}
	
	// save workflow Object in the new user
	@Override
	public void register(UserDTO userDTO) {
		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("username").is(userDTO.getUsername())), UserDTO.class, "user");
		if (dto != null) {
			throw new ValidationException("the user already exists");
		}
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
		userDTO.setPassword(hashedPassword);
		Random rnd = new Random();
		double account_number = 10000000 + rnd.nextInt(90000000);
		userDTO.setAccount_number(account_number);
		WorkflowDTO workDTO=new WorkflowDTO();
		workDTO.setType(env.getProperty("type.register"));
		UserType usertype = null;
//		UserType usertype=userDTO.getRole();
//		if(usertype!=UserType.Tier1 || usertype!=UserType.Tier2 || usertype!=UserType.Customer)
//			throw new ValidationException("Invalid user role");
		List<UserDTO> details=new ArrayList<UserDTO>();
		details.add(userDTO);
		workDTO.setDetails(details);
		workDTO.setRole(usertype.Tier2); // hardCoded
		mongoTemplate.save(workDTO, "workflow");
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
		EmailService es = new EmailService();
		String subject = "One Time Password (OTP) for Login";
		if(!es.send_email(dto.getUsername(), dto.getEmail(), subject)) {
			throw new BusinessException("Error in sending the email！");
		}
		dto.setPassword(null);
		return dto;
	}
	@Override
	public UserDTO updateUserInfo( UserDTO user) {

		WorkflowDTO workDTO=new WorkflowDTO();
		workDTO.setType(env.getProperty("type.updateUserInfo"));
//		UserType usertype=userDTO.getRole();
//		if(usertype!=UserType.Tier1 || usertype!=UserType.Tier2 || usertype!=UserType.Customer)
//			throw new ValidationException("Invalid user role");
		List<UserDTO> details=new ArrayList<UserDTO>();
		details.add(user);
		workDTO.setDetails(details);
		UserType usertype = null;
		workDTO.setRole(usertype.Tier2);
		mongoTemplate.save(workDTO, "workflow");
		return user;
	}

	@Override
	public WorkflowDTO updateDetails( WorkflowDTO workflowDTO) {
		LinkedHashMap map = (LinkedHashMap) workflowDTO.getDetails().get(0);
		Update update = new Update();
		if(map.get("address")!=null) {
			update.set("address", map.get("address").toString());
		}
		if(map.get("email")!=null) {
			update.set("email", map.get("email").toString());
		}
		
		UpdateResult userObj = mongoTemplate.updateFirst(Query.query(Criteria.where("username").is(map.get("username").toString())), update, User.class, "user");
		if (userObj == null) {
			throw new BusinessException("cannot be updated！");
		}
		return workflowDTO;
	}
	

	@Override
	public boolean checkAndMatchOTP(String username, String otp) {
		MongoCollection<Document> collection = database.getCollection("loginOTP");
		Document myDoc = collection.find(eq("username", username)).first();
		String otp_db = myDoc.get("otp").toString();
		if (otp_db.equals(otp)) {
			collection.updateOne(eq("username", username), new Document("$set", new Document("verified", true)));
			return true;
		}
		return false;
	}

	@Override
	public boolean forgotPasswordOTP(String username) {
		
		MongoCollection<Document> collection = database.getCollection("user");
		Document myDoc = collection.find(eq("username", username)).first();
		String email = myDoc.get("email").toString();
		if (email.isEmpty()) return false;
		EmailService es = new EmailService();
		String subject = "SBS Bank Password Reset OTP";
		es.send_email(username, email, subject);
		return true;
	}

	@Override
	public WorkflowDTO createUser(WorkflowDTO workflowDTO) {
//		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("username").is(userDTO.getUsername())), UserDTO.class, "user");
//		if (dto != null) {
//			throw new ValidationException("the user already exists");
//		}
		LinkedHashMap map = (LinkedHashMap) workflowDTO.getDetails().get(0);
		mongoTemplate.save(workflowDTO.getDetails().get(0), "user");
		AuthenticationProfileDTO authenticationProfileDTO = new AuthenticationProfileDTO();
		authenticationProfileDTO.setPassword(map.get("password").toString());
		authenticationProfileDTO.setUsername(map.get("username").toString());
		mongoTemplate.save(authenticationProfileDTO, "authenticationProfile");
		return workflowDTO;
		
	}

}
