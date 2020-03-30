package io.sbs.dto;


public class AccountDTO {
	
	private String acc_type;
    private double acc_balance;
    private double account_number;
    private String username;
    public double getAcc_balance() {
		return acc_balance;
	}

	public void setAcc_balance(double acc_balance) {
		this.acc_balance = acc_balance;
	}

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
	public void setUsername(String username) {
        this.username = username;
    }
	public String getUsername() {
        return username;
    }
	

}
