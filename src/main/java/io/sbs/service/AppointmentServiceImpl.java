package io.sbs.service;

import io.sbs.dto.UserDTO;
import io.sbs.dto.WorkflowDTO;
import io.sbs.exception.BusinessException;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Service
public class AppointmentServiceImpl implements AppointmentService{

	final MongoClient mongoClient = MongoClients
			.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	final MongoDatabase database = mongoClient.getDatabase("mydb");
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public WorkflowDTO createAppointments(WorkflowDTO workflowDTO) {
//		// TODO Auto-generated method stub
		
		LinkedHashMap map = (LinkedHashMap) workflowDTO.getDetails().get(0);
		System.out.println(mongoTemplate);
		UserDTO dto = mongoTemplate.findOne(Query.query(Criteria.where("username").is(map.get("username").toString())), UserDTO.class, "user");
		
		if (dto == null) {
			throw new BusinessException("User not found!");
		}
		
		EmailService es = new EmailService();
		String subject = "Appointment created";
		if(!es.send_email(dto.getUsername(), dto.getEmail(), subject)) {
			throw new BusinessException("Error in sending the email！");
		}
		return workflowDTO;
	}

}
