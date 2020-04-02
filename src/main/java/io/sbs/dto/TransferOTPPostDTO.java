package io.sbs.dto;

public class TransferOTPPostDTO {

	private String mode;
	private String fromAccNo;
	private String toBeneficiary;
	private String description;
	private double amount;
	private boolean self;
	private String otp;

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public boolean isSelf() {
		return self;
	}

	public void setSelf(boolean self) {
		this.self = self;
	}

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
		return fromAccNo;
	}

	public void setFrom_accnt(String from_accnt) {
		this.fromAccNo = from_accnt;
	}

	public String gettoBeneficiary() {
		return toBeneficiary;
	}

	public void settoBeneficiary(String toBeneficiary) {
		this.toBeneficiary = toBeneficiary;
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

	public String getFromAccNo() {
		return fromAccNo;
	}

	public void setFromAccNo(String fromAccNo) {
		this.fromAccNo = fromAccNo;
	}

}
