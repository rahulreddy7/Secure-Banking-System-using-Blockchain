package io.sbs.service;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import io.sbs.constant.StringConstants;
import io.sbs.constant.UserType;
import io.sbs.dto.AccountDTO;
import io.sbs.dto.AppointmentDTO;
import io.sbs.dto.AuthenticationProfileDTO;
import io.sbs.dto.CustomDTO;
import io.sbs.dto.CustomWorkflowDTO;
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
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	
	final private int Max_Attempts=3;
	final private int Lock_Timeout=30;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private Environment env;
	private static ApplicationContext applicationContext;
	
	private Logger logger = LogManager.getLogger();
	
	@Override
	public ResponseEntity<?> getUserAccountDetails(String username) {
		
		logger.info("In getUserAccountDetails API service.");
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
		logger.info("In getUserInfo API service.");
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
		logger.info("In register API service.");

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
		logger.info("In login API service.");
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("username").is(userDTO.getUsername())), UserDTO.class, "authenticationProfile");
		Query query=Query.query(Criteria.where("username").is(userDTO.getUsername()));
		Update update1=new Update();
		if (dto == null) {
			throw new BusinessException("the account doesn't exist！");
		}

		if (!passwordEncoder.matches(userDTO.getPassword(), dto.getPassword())) {
			if(dto.getAttempts()<Max_Attempts) {
				int count=dto.getAttempts()+1;
				update1.set("attempts",count);
				update1.set("lastModified", new Date());
				mongoTemplate.findAndModify(query, update1, UserDTO.class,"authenticationProfile");
				throw new BusinessException("password is wrong! "+(Max_Attempts-dto.getAttempts())+" more attempts left");
			}else {
				update1.set("isLocked","true");
				update1.set("lastModified", new Date());
				mongoTemplate.findAndModify(query, update1, UserDTO.class,"authenticationProfile");
				throw new BusinessException("Account is locked for "+Lock_Timeout+" mins");
			}
			
		}
		if(dto.getIsLocked()!=null && dto.getIsLocked().equals("true")) {
			long duration=new Date().getTime()-dto.getLastModified().getTime();
			long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
			if(diffInMinutes>Lock_Timeout) {
				update1.set("isLocked", "false");
				update1.set("attempts",0);
				update1.set("lastModified",new Date());
				mongoTemplate.findAndModify(query, update1, UserDTO.class,"authenticationProfile");
			}else {
				throw new BusinessException("Account is locked for another " +(Lock_Timeout-diffInMinutes)+" mins");
			}
		}else {
			update1.set("attempts",0);
			update1.set("lastModified",new Date());
			mongoTemplate.findAndModify(query, update1, UserDTO.class,"authenticationProfile");
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
		logger.info("In updateUserInfo API service.");
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
		logger.info("In updateDetails API service.");
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
		logger.info("In checkAndMatchOTP API service.");
		MongoCollection<Document> collection = database.getCollection("loginOTP");
		Document myDoc = collection.find(and(eq("username", username), eq("verified", false))).first();
		if (myDoc == null || myDoc.get("otp") == null)
			return false;

		String otp_db = myDoc.get("otp").toString();
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if (passwordEncoder.matches(otp, otp_db)) {
			collection.updateOne(eq("username", username), new Document("$set", new Document("verified", true)));
			return true;
		} else return false;

	}

	@Override
	public boolean forgotPasswordOTP(String username) {
		logger.info("In forgotPasswordOTP API service.");
		String role = getRoleGeneric(username).toString();
		if (role == null)
			return false;
		
		MongoCollection<Document> collection = null;
		Document myDoc = null;
		String email = null;
		if (role.equalsIgnoreCase("Customer")) {
			collection = database.getCollection("user");
			myDoc = collection.find(eq("username", username)).first();
			if (myDoc.get("email") == null) return false;
			email = myDoc.get("email").toString();
		} else {
			collection = database.getCollection("employee");
			myDoc = collection.find(eq("username", username)).first();
			if (myDoc.get("employee_email") == null) return false;
			email = myDoc.get("employee_email").toString();
		}
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
		logger.info("In createUser API service.");
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

	public ResponseEntity<?> resetPass(UserDTO user) {
		logger.info("In resetPass API service.");
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("username").is(user.getUsername())), UserDTO.class, "authenticationProfile");
		if (dto == null) {
			return new ResponseEntity<>("No linked username exists.", HttpStatus.BAD_REQUEST);
		}

		if (!checkAndMatchOTP(user.getUsername(), user.getOtp()))
			return new ResponseEntity<>("OTP doesn't match.", HttpStatus.BAD_REQUEST);

		String hashedPassword = passwordEncoder.encode(user.getPassword());
		MongoCollection<Document> c = database.getCollection("authenticationProfile");
		c.updateOne(eq("username", user.getUsername()), new Document("$set", new Document("password", hashedPassword)));
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> addAccToWorkflow(String username, Account acc) {
		logger.info("In addAccToWorkflow API service.");
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
		logger.info("In createNewAcc API service.");
		LinkedHashMap map = (LinkedHashMap) workflowDTO.getDetails().get(0);
		mongoTemplate.save(workflowDTO.getDetails().get(0), "account");
		return workflowDTO;
	}


	@Override
	public ResponseEntity<?> generateChequeService(Account acc) {
		logger.info("In generateChequeService API service.");
		MongoCollection<Document> acc_collection = database.getCollection("account");
		Document myDoc = acc_collection.find(eq("account_number", acc.getAccount_number())).first();
		if (myDoc == null)
			return new ResponseEntity<>("No accounts linked to this number.", HttpStatus.BAD_REQUEST);
		
		String username = myDoc.getString("username");
		
		MongoCollection<Document> user_collection = database.getCollection("user");
		Document userDoc = user_collection.find(eq("username", username)).first();
		if (userDoc == null || userDoc.getString("email") == null )
			return new ResponseEntity<>("No linked username or email to this account number.", HttpStatus.BAD_REQUEST);
		String email = userDoc.getString("email");
		EmailService es = new EmailService();

		double balance = myDoc.getDouble("acc_balance");
		if (acc.getAmount_to_debit() > balance)
			return new ResponseEntity<>("Insufficient balance.", HttpStatus.BAD_REQUEST);

		balance = balance - acc.getAmount_to_debit();
		acc_collection.updateOne(eq("account_number", acc.getAccount_number()), new Document("$set", new Document("acc_balance", balance)));
		es.send_email_cheque_success(email, "Cashier Cheque Issued", acc.getAmount_to_debit());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> creditAmountService(Account acc) {
		logger.info("In creditAmountService API service.");
		MongoCollection<Document> collection = database.getCollection("account");
		Document myDoc = collection.find(eq("account_number", acc.getAccount_number())).first();
		if (myDoc == null)
			return new ResponseEntity<>("No account found. ", HttpStatus.OK);
		
		double balance = myDoc.getDouble("acc_balance");
		balance += acc.getAmount_to_credit();
		collection.updateOne(eq("account_number", acc.getAccount_number()), new Document("$set", new Document("acc_balance", balance)));
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@Override
	public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
		logger.info("In createAppointment API service.");
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
		logger.info("In createAppointments API service.");
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
		logger.info("In updateStateOfWorkflow API service.");
		Update update = new Update();
		update.set("state", workflowDTO.getState());
		UpdateResult userObj = mongoTemplate.updateFirst(Query.query(Criteria.where("workflow_id").is(workflowDTO.getWorkflow_id())), update, WorkflowDTO.class, "workflow");
		return workflowDTO;
	}

	@Override
	public UserType getUserRole(String username) {
		logger.info("In getUserRole API service.");
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

	@Override
	public UserType getRoleGeneric(String username) {
		logger.info("In getRoleGeneric API service.");
		MongoCollection<Document> collection = database.getCollection("authenticationProfile");
		Document myDoc = collection.find(eq("username", username)).first();
		if (myDoc == null)
			return null;
		String role = myDoc.get("role").toString();
		if (role.equalsIgnoreCase("Tier1"))
			return UserType.Tier1;
		else if (role.equalsIgnoreCase("Tier2"))
			return UserType.Tier2;
		else if (role.equalsIgnoreCase("Customer"))
			return UserType.Customer;
		else if (role.equalsIgnoreCase("Admin"))
			return UserType.Admin;
		else
			return null;
	}

  public WorkflowDTO deleteWorkflowObj(WorkflowDTO workflowDTO) {
	  	logger.info("In deleteWorkflowObj API service.");
		MongoCollection<Document> collection = database.getCollection("workflow");
		collection.deleteOne(eq("workflow_id", workflowDTO.getWorkflow_id()));
		return null;
	}

	@Override
	public List<WorkflowDTO> getAllWorkflows(String username) {
		logger.info("In getAllWorkflows API service.");
		UserDTO userDTO = mongoTemplate.findOne(new Query(Criteria.where("username").is(username)),UserDTO.class,"authenticationProfile");

		Criteria criteria = new Criteria();
		criteria = criteria.and("role").is(userDTO.getRole());
		criteria = criteria.and("state").is(StringConstants.WORKFLOW_PENDING);
		
		List<WorkflowDTO> workflows = mongoTemplate.find(new Query(criteria),WorkflowDTO.class,"workflow");
		return workflows;
	}
	
	public WorkflowDTO findWorkflowObj(CustomWorkflowDTO workflow) {
		logger.info("In findWorkflowObj API service.");
		WorkflowDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("workflow_id").is(workflow.getWorkflow_id())), WorkflowDTO.class, "workflow");
		if (dto == null) {
			throw new BusinessException("Workflow not found!");
		}
		return dto;
	}

	@Override
	public ResponseEntity<?> deleteAccService(Account acc) {
		logger.info("In deleteAccService API service.");
		MongoCollection<Document> collection = database.getCollection("account");
		Document myDoc = collection.find(eq("account_number", acc.getAccount_number())).first();
		if (myDoc == null)
			return new ResponseEntity<>("No account found. ", HttpStatus.OK);

		String username = myDoc.getString("username");

		Long count = collection.countDocuments(eq("username", username));
		if (count == 1)
			return new ResponseEntity<>("Only one account linked with this user.",HttpStatus.BAD_REQUEST);
		collection.deleteOne(eq("account_number", acc.getAccount_number()));
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
