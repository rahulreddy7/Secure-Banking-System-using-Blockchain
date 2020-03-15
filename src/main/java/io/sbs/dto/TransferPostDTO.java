package io.sbs.dto;

public class TransferPostDTO {

	private String mode;
	private String from_accnt;
	private String to_accnt;
	private String description;
	private double amount;
	
	private long phoneNumber;
	private String email;
	

	public long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
