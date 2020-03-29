package io.sbs.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import io.sbs.constant.UserType;
import io.sbs.model.Account;

public class WorkflowDTO implements Serializable{
	private String type;
	private String workflow_id = UUID.randomUUID().toString();
	private List<?> details;
	private UserType role;
	private String state;// Pending, Approved, Declined
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public UserType getRole() {
		return role;
	}
	public void setRole(UserType role) {
		this.role = role;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<?> getDetails() {
		return details;
	}
	public void setDetails(List<?> details) {
		this.details = details;
	}
	public String getWorkflow_id() {
		return workflow_id;
	}
	
	
}
