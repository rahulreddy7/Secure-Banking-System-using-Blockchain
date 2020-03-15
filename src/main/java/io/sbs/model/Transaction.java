package io.sbs.model;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Transaction")
public class Transaction {

	private String from_accnt;
	private String to_accnt;
	private String description;
	private String transaction_type;
	private double amount;
	private Date creationTime;
	private Date updateTime;
	public String getFrom_accnt() {
		return from_accnt;
	}
	public void setFrom_accnt(String from_accnt) {
		this.from_accnt = from_accnt;
	}
	public String getTo_accnt() {
		return to_accnt;
	}
	public void setTo_accnt(String to_accnt) {
		this.to_accnt = to_accnt;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTransaction_type() {
		return transaction_type;
	}
	public void setTransaction_type(String transaction_type) {
		this.transaction_type = transaction_type;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	
}
