package io.sbs.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Accounts")
public class Account {
	
	@NotNull
	private String acc_type;
	private String acc_holder_name;
	@NotNull
    private double acc_balance;
    private String username;
    private String account_number;
    private double amount_to_debit;
    private double amount_to_credit;
	
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public double getAmount_to_debit() {
		return amount_to_debit;
	}
	public void setAmount_to_debit(double amount_to_deduct) {
		this.amount_to_debit = amount_to_deduct;
	}
	public String getAccount_number() {
		return account_number;
	}
	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}
	public double getAmount_to_credit() {
		return amount_to_credit;
	}
	public void setAmount_to_credit(double amount_to_credit) {
		this.amount_to_credit = amount_to_credit;
	}


}
