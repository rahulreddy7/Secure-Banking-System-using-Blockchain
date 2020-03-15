package io.sbs.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Accounts")
public class Account {
	
	private String acc_type;
	private String acc_holder_name;
    private double acc_balance;
    private String user_id;
    private double account_number;
	
    public double getAccount_number() {
		return account_number;
	}
	public void setAccount_number(double account_number) {
		this.account_number = account_number;
	}
	public String getAcc_type() {
		return acc_type;
	}
	public void setAcc_type(String acc_type) {
		this.acc_type = acc_type;
	}
	public String getAcc_holder_name() {
		return acc_holder_name;
	}
	public void setAcc_holder_name(String acc_holder_name) {
		this.acc_holder_name = acc_holder_name;
	}
	public double getAcc_balance() {
		return acc_balance;
	}
	public void setAcc_balance(double acc_balance) {
		this.acc_balance = acc_balance;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

}
