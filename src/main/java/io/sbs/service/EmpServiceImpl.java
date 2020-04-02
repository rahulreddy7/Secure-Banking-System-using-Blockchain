package io.sbs.service;

import static com.mongodb.client.model.Filters.eq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.sbs.model.Employee;

@Service
public class EmpServiceImpl implements EmpService{
	
	MongoClient mongoClient = MongoClients.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	MongoDatabase database = mongoClient.getDatabase("mydb");
	
	private Logger logger = LogManager.getLogger();
	
	@Override
	public ResponseEntity<?> addNewEmpService(Employee e, String username) {
		logger.info("In addNewEmpService API service.");
		MongoCollection<Document> collection = database.getCollection("employee");
		Document myDoc = collection.find(eq("username", username)).first();
		if (myDoc != null)
			return new ResponseEntity<>("This user already exists. ", HttpStatus.BAD_REQUEST);

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(e.getEmployee_password());
		Document doc = new Document("username", username)
                .append("employee_password", hashedPassword)
                .append("employee_name", e.getEmployee_name())
                .append("employee_role", e.getEmployee_role())
                .append("employee_phone", e.getEmployee_phone())
                .append("employee_email", e.getEmployee_email())
                .append("employee_address", e.getEmployee_address());
		collection.insertOne(doc);

		collection = database.getCollection("authenticationProfile");
		Document myDoc2 = collection.find(eq("username", username)).first();
		Document authenticationProfileDTO = new Document("username", username).append("password", hashedPassword).append("role", e.getEmployee_role());
		if (myDoc2 == null) collection.insertOne(authenticationProfileDTO);
		else collection.updateOne(eq("username",username), new Document("$set", new Document("password", hashedPassword).append("role", e.getEmployee_role())));
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> modifyEmpService(Employee e) {
		logger.info("In modifyEmpService API service.");
		MongoCollection<Document> collection = database.getCollection("employee");
		Document myDoc = collection.find(eq("username", e.getUsername())).first();
		if (myDoc == null)
			return new ResponseEntity<>("The user does not exist. ", HttpStatus.BAD_REQUEST);
		
		Document update = new Document();
		if (e.getEmployee_address() != null) update.append("employee_address", e.getEmployee_address());
		if (e.getEmployee_name() != null) update.append("employee_name", e.getEmployee_name());
		if (e.getEmployee_role() != null) update.append("employee_role", e.getEmployee_role());
		if (e.getEmployee_phone() != null) update.append("employee_phone", e.getEmployee_phone());
		if (e.getEmployee_email() != null) update.append("employee_email", e.getEmployee_email());
		if (e.getEmployee_password() != null) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(e.getEmployee_password());
			update.append("employee_password", hashedPassword);
			MongoCollection<Document> coll_authentication = database.getCollection("authenticationProfile");
			coll_authentication.updateOne(eq("username", e.getUsername()), new Document("$set", new Document("password", hashedPassword)));
		}
		collection.updateOne(eq("username", e.getUsername()), new Document("$set", update));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> deleteEmpService(Employee e, String username) {
		logger.info("In deleteEmpService API service.");
		MongoCollection<Document> collection = database.getCollection("employee");
		Document myDoc = collection.find(eq("username", username)).first();
		if (myDoc == null)
			return new ResponseEntity<>("The user does not exist. ", HttpStatus.BAD_REQUEST);
		collection.deleteMany(eq("username", username));
		
		collection = database.getCollection("authenticationProfile");
		collection.deleteMany(eq("username", username));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> viewEmpService(String username) {
		logger.info("In viewEmpService API service.");
		MongoCollection<Document> collection = database.getCollection("employee");
		Document myDoc = collection.find(eq("username", username)).first();
		if (myDoc == null)
			return new ResponseEntity<>("The user does not exist. ", HttpStatus.BAD_REQUEST);
		Employee e = new Employee();
		e.setEmployee_email(myDoc.getString("employee_email"));
		e.setEmployee_name(myDoc.getString("employee_name"));
		e.setEmployee_phone(myDoc.getString("employee_phone"));
		e.setEmployee_address(myDoc.getString("employee_address"));
		e.setEmployee_role(myDoc.getString("employee_role"));
		return new ResponseEntity<>(e, HttpStatus.OK);
	}

}
