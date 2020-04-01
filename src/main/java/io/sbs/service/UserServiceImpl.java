package io.sbs.service;

import static com.mongodb.client.model.Filters.eq;
import io.sbs.constant.StringConstants;
import io.sbs.constant.UserType;
import io.sbs.dto.AccountDTO;
import io.sbs.dto.AppointmentDTO;
import io.sbs.dto.AuthenticationProfileDTO;
import io.sbs.dto.CustomDTO;
import io.sbs.dto.UserDTO;
import io.sbs.dto.WorkflowDTO;
import io.sbs.exception.BusinessException;
import io.sbs.exception.ValidationException;
import io.sbs.model.Account;
import io.sbs.model.Employee;
import io.sbs.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

@Service
public class UserServiceImpl implements UserService {
	
	final MongoClient mongoClient = MongoClients.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	final MongoDatabase database = mongoClient.getDatabase("mydb");

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private Environment env;
	private static ApplicationContext applicationContext;
	
	@Override
	public ResponseEntity<?> getUserAccountDetails(String username) {

	    MongoCollection<Document> collection = database.getCollection("user");
	    Document myDoc = collection.find(eq("username", username)).first();
	    
	    if (myDoc == null)
	    	return new ResponseEntity<>("No username exists in DB!",HttpStatus.NO_CONTENT);
	    
	    MongoCollection<Document> collection_acc = database.getCollection("account");
	    List<Document> cursor_accounts = collection_acc.find(eq("username", username)).into(new ArrayList<Document>());
	    
	    if (cursor_accounts == null)
	    	return new ResponseEntity<>("No accounts linked to this username!",HttpStatus.NO_CONTENT);
	    
		List<Account> acc_list = new ArrayList<Account>();
	    for (Document account : cursor_accounts) {
	    	Account a = new Account();
	    	if (myDoc.get("name").toString() != null) a.setAcc_holder_name(myDoc.get("name").toString());
	    	if (account.get("account_number").toString() != null) a.setAccount_number(account.get("account_number").toString());
	    	if (account.get("acc_type").toString() != null) a.setAcc_type(account.get("acc_type").toString());
	    	if (account.get("acc_balance").toString() != null) a.setAcc_balance(Double.parseDouble(account.get("acc_balance").toString()));
	        a.setUsername(username);
	        acc_list.add(a);
	    }
		if (acc_list.size() > 0)
			return new ResponseEntity<>(acc_list, HttpStatus.OK);
		else
			return new ResponseEntity<>("No Records Found!",HttpStatus.NO_CONTENT);
	}

	@Override
	public User getUserInfo(String username) {
		MongoCollection<Document> collection = database.getCollection("user");
		Document myDoc = collection.find(eq("username", username)).first();
		User user = new User();
		if (myDoc.get("name") != null) user.setName(myDoc.get("name").toString());
		if (myDoc.get("email") != null) user.setEmailString(myDoc.get("email").toString());
		if (myDoc.get("address") != null) user.setAddress(myDoc.get("address").toString());
		if (myDoc.get("phone") != null) user.setPhone(myDoc.get("phone").toString());
		return user;
	}
	
	// save workflow Object in the new user
	@Override
	public void register(CustomDTO customDTO) {
		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("username").is(customDTO.getUsername())), UserDTO.class, "user");
		if (dto != null) {
			throw new ValidationException("the user already exists");
		}
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(customDTO.getPassword());
		customDTO.setPassword(hashedPassword);
		Date date= new Date();
		customDTO.setCreated_at(date.toString());
		customDTO.setUpdated_at(date.toString());
		Random rnd = new Random();
		long account_number = 10000000 + rnd.nextInt(90000000);
		String acc_num = String.valueOf((account_number));
		customDTO.setAccount_number(acc_num);
		if(customDTO.getAcc_balance()<=0.0)
			throw new ValidationException("Minimum account balance needed");
		WorkflowDTO workDTO=new WorkflowDTO();
		workDTO.setType(env.getProperty("type.register"));
		List<CustomDTO> details=new ArrayList<CustomDTO>();
		details.add(customDTO);
		workDTO.setDetails(details);
		UserType usertype=null;
		workDTO.setRole(usertype.Tier2); // hardCoded
		workDTO.setState("Pending");
		mongoTemplate.save(workDTO, "workflow");
	}

	
	@Override
	public ResponseEntity<?> login(UserDTO userDTO) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("username").is(userDTO.getUsername())), UserDTO.class, "authenticationProfile");
		if (dto == null) {
			throw new BusinessException("the account doesn't exist！");
		}

		if (!passwordEncoder.matches(userDTO.getPassword(), dto.getPassword())) {
			throw new BusinessException("password is wrong！");
		}
		
		String role = dto.getRole().toString();
		if (role.equalsIgnoreCase("Customer")) {
			UserDTO dto2 = mongoTemplate.findOne(Query.query(Criteria.where("username").is(userDTO.getUsername())), UserDTO.class, "user");
			if (dto2 != null) {
				EmailService es = new EmailService();
				String subject = "One Time Password (OTP) for Login";
				if(!es.send_email(dto2.getUsername(), dto2.getEmail(), subject)) {
					throw new BusinessException("Error in sending the email！");
				}
				dto2.setPassword(null);
				return new ResponseEntity<>(HttpStatus.OK);
			} 
		} else {
			Employee dto3 = mongoTemplate.findOne(Query.query(Criteria.where("username").is(userDTO.getUsername())), Employee.class, "employee");
			if (dto3 != null) {
				EmailService es = new EmailService();
				String subject = "One Time Password (OTP) for Login";
				if(!es.send_email(dto3.getUsername(), dto3.getEmployee_email(), subject)) {
					throw new BusinessException("Error in sending the email！");
				}
				dto3.setEmployee_password(null);
				return new ResponseEntity<>(HttpStatus.OK);
			}
		}
		return new ResponseEntity<>("No records found.", HttpStatus.OK);
	}
	@Override
	public UserDTO updateUserInfo( UserDTO user) {

		WorkflowDTO workDTO=new WorkflowDTO();
		workDTO.setType(env.getProperty("type.updateUserInfo"));
//		UserType usertype=userDTO.getRole();
//		if(usertype!=UserType.Tier1 || usertype!=UserType.Tier2 || usertype!=UserType.Customer)
//			throw new ValidationException("Invalid user role");
		List<UserDTO> details=new ArrayList<UserDTO>();
		Date date = new Date();
		user.setUpdated_at(date.toString());
		details.add(user);
		workDTO.setDetails(details);
		UserType usertype = null;
		workDTO.setRole(usertype.Tier2);
		workDTO.setState("Pending");
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
		if(map.get("phone")!=null) {
			update.set("phone", map.get("phone").toString());
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
		AccountDTO accountDTO = new AccountDTO();
		UserDTO userDTO = new UserDTO();
		AuthenticationProfileDTO authenticationProfileDTO = new AuthenticationProfileDTO();
		userDTO.setPassword(map.get("password").toString());
		if(map.get("name")!=null)
			userDTO.setName(map.get("name").toString());
		if(map.get("phone")!=null)
			userDTO.setPhone(map.get("phone").toString());
		userDTO.setUsername(map.get("username").toString());
		if(map.get("email")!=null)
			userDTO.setEmail(map.get("email").toString());
		if(map.get("address")!=null)
			userDTO.setAddress(map.get("address").toString());
		String role = map.get("role").toString();
		if(role.equals("Tier1")) {
			System.out.println(role);
			authenticationProfileDTO.setRole(UserType.Tier1);
			userDTO.setRole(UserType.Tier1);
		}
		else if(role.equals("Tier2")) {
			authenticationProfileDTO.setRole(UserType.Tier2);
			userDTO.setRole(UserType.Tier2);
		}	
		else if(role.equals("Customer")) {
			userDTO.setRole(UserType.Customer);
			authenticationProfileDTO.setRole(UserType.Customer);
		}
			
		
//			throw new ValidationException("Invalid user role");
		Date date= new Date();
		userDTO.setCreated_at(date.toString());
		userDTO.setUpdated_at(date.toString());
		String acc_num = map.get("account_number").toString();
		double acc_bal = Double.parseDouble(map.get("acc_balance").toString());
		accountDTO.setAccount_number(acc_num);
		accountDTO.setAcc_balance(acc_bal);
		accountDTO.setUsername(map.get("username").toString());
		accountDTO.setAcc_type(map.get("acc_type").toString());
		mongoTemplate.save(userDTO, "user");
		mongoTemplate.save(accountDTO, "account");
		
		
		authenticationProfileDTO.setPassword(map.get("password").toString());
		authenticationProfileDTO.setUsername(map.get("username").toString());
		
		mongoTemplate.save(authenticationProfileDTO, "authenticationProfile");
		return workflowDTO;
	}
	
	
		

	public ResponseEntity<?> resetPass(String username, String currpassword, String newpassword) {

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), UserDTO.class, "authenticationProfile");
		if (dto == null) {
			return new ResponseEntity<>("No username exists.", HttpStatus.BAD_REQUEST);
		}

		if (!passwordEncoder.matches(currpassword, dto.getPassword())) {
			return new ResponseEntity<>("Password does not match.", HttpStatus.FORBIDDEN);
		}
		String hashedPassword = passwordEncoder.encode(newpassword);
		MongoCollection<Document> c = database.getCollection("authenticationProfile");
		c.updateOne(eq("username", username), new Document("$set", new Document("password", hashedPassword)));
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> addAccToWorkflow(String username, Account acc) {
		EmailService es = new EmailService();
		String acc_num = new String(es.generate_random(10));

		MongoCollection<Document> collection = database.getCollection("user");
		Document myDoc = collection.find(eq("username", username)).first();
		if (myDoc == null)
			return new ResponseEntity<>("No user found. ", HttpStatus.OK);
		
		collection = database.getCollection("account");
		myDoc = collection.find(eq("account_number", acc_num)).first();
		if (myDoc != null)
			acc_num = new String(es.generate_random(10));
		acc.setAccount_number(acc_num);
		acc.setUsername(username);
		WorkflowDTO workDTO=new WorkflowDTO();
		workDTO.setType(StringConstants.WORKFLOW_NEW_ACC);
		
		List<Account> details=new ArrayList<Account>();
		details.add(acc);
		workDTO.setDetails(details);
		workDTO.setRole(UserType.Tier2);
		workDTO.setState("Pending");
		mongoTemplate.save(workDTO, "workflow");
		return new ResponseEntity<>(acc, HttpStatus.OK);
	}
	
	@Override
	public WorkflowDTO createNewAcc(WorkflowDTO workflowDTO) {
		LinkedHashMap map = (LinkedHashMap) workflowDTO.getDetails().get(0);
		mongoTemplate.save(workflowDTO.getDetails().get(0), "account");
		return workflowDTO;
	}


	@Override
	public ResponseEntity<?> generateChequeService(String username, Account acc) {
		MongoCollection<Document> collection = database.getCollection("user");
		Document myDoc = collection.find(eq("username", username)).first();
		String email = myDoc.getString("email");
		EmailService es = new EmailService();
		collection = database.getCollection("account");
		myDoc = collection.find(eq("username", username)).first();
		if (myDoc == null)
			return new ResponseEntity<>("No username found. ", HttpStatus.OK);

		myDoc = collection.find(eq("account_number", acc.getAccount_number())).first();
		if (myDoc == null)
			return new ResponseEntity<>("No account found. ", HttpStatus.OK);

		double balance = myDoc.getDouble("acc_balance");
		balance = balance - acc.getAmount_to_debit();
		collection.updateOne(eq("account_number", acc.getAccount_number()), new Document("$set", new Document("acc_balance", balance)));
		es.send_email_cheque_success(email, "Cashier Cheque Issued", acc.getAmount_to_debit());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> debitAmountService(String username, Account acc) {
		MongoCollection<Document> collection = database.getCollection("account");
		Document myDoc = collection.find(eq("username", username)).first();
		if (myDoc == null)
			return new ResponseEntity<>("No username found. ", HttpStatus.OK);

		myDoc = collection.find(eq("account_number", acc.getAccount_number())).first();
		if (myDoc == null)
			return new ResponseEntity<>("No account found. ", HttpStatus.OK);
		
		double balance = myDoc.getDouble("acc_balance");
		balance += acc.getAmount_to_credit();
		collection.updateOne(eq("account_number", acc.getAccount_number()), new Document("$set", new Document("acc_balance", balance)));
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@Override
	public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
		WorkflowDTO workDTO=new WorkflowDTO();
		workDTO.setType(env.getProperty("type.createAppointment"));
//		UserType usertype=userDTO.getRole();
//		if(usertype!=UserType.Tier1 || usertype!=UserType.Tier2 || usertype!=UserType.Customer)
//			throw new ValidationException("Invalid user role");
		List<AppointmentDTO> details=new ArrayList<AppointmentDTO>();
		Date date = new Date();  
		appointmentDTO.setCreated_at(date);
		details.add(appointmentDTO);
		workDTO.setDetails(details);
		UserType usertype = null;
		workDTO.setRole(usertype.Tier1);
		workDTO.setState("Pending");
		mongoTemplate.save(workDTO, "workflow");
		return appointmentDTO;
	}
	
	@Override
	public WorkflowDTO createAppointments(WorkflowDTO workflowDTO) {
//		// TODO Auto-generated method stub
		
		LinkedHashMap map = (LinkedHashMap) workflowDTO.getDetails().get(0);
		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("username").is(map.get("username").toString())), UserDTO.class, "user");
		
		if (dto == null) {
			throw new BusinessException("User not found!");
		}
		
		EmailService es = new EmailService();
		String subject = "Appointment created";
		String appointment_details = map.get("date")+ " at " +map.get("time")+ "<br>"+map.get("details");
		if(!es.send_email_appointment(dto.getUsername(), dto.getEmail(), subject, appointment_details)) {
			throw new BusinessException("Error in sending the email！");
		}
		return workflowDTO;
	}
	@Override
	public WorkflowDTO updateStateOfWorkflow(WorkflowDTO workflowDTO) {
		Update update = new Update();
		update.set("state", workflowDTO.getState());
		UpdateResult userObj = mongoTemplate.updateFirst(Query.query(Criteria.where("workflow_id").is(workflowDTO.getWorkflow_id())), update, WorkflowDTO.class, "workflow");
		return workflowDTO;
	}

	@Override
	public UserType getUserRole(String username) {
		MongoCollection<Document> collection = database.getCollection("user");
		Document myDoc = collection.find(eq("username", username)).first();
		if (myDoc == null)
			return null;
		String role = myDoc.get("role").toString();
		if (role.equalsIgnoreCase("Tier1"))
			return UserType.Tier1;
		else if (role.equalsIgnoreCase("Tier2"))
			return UserType.Tier2;
		else
			return UserType.Customer;
	}

  public WorkflowDTO deleteWorkflowObj(WorkflowDTO workflowDTO) {
		MongoCollection<Document> collection = database.getCollection("workflow");
		collection.deleteOne(eq("workflow_id", workflowDTO.getWorkflow_id()));
		return null;
	}

@Override
public List<WorkflowDTO> getAllWorkflows(String username) {
	UserDTO userDTO = mongoTemplate.findOne(new Query(Criteria.where("username").is(username)),UserDTO.class,"authenticationProfile");
	System.out.println(username + " sad ");
	Criteria criteria = new Criteria();
	criteria = criteria.and("role").is(userDTO.getRole().toString());
	criteria = criteria.and("state").is(StringConstants.WORKFLOW_PENDING);
	
	List<WorkflowDTO> workflows = mongoTemplate.find(new Query(criteria),WorkflowDTO.class,"workflow");
	return workflows;
}

public WorkflowDTO findWorkflowObj(WorkflowDTO workflow) {
	WorkflowDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("workflow_id").is(workflow.getWorkflow_id())), WorkflowDTO.class, "workflow");
	if (dto == null) {
		throw new BusinessException("Workflow not found!");
	}
	return dto;
}

}
