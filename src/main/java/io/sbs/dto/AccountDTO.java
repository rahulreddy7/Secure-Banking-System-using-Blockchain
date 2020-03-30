package io.sbs.dto;


public class AccountDTO {
	
	private String acc_type;
    private double acc_balance;
    private String account_number;
    private String username;
    public double getAcc_balance() {
		return acc_balance;
	}

	public void setAcc_balance(double acc_balance) {
		this.acc_balance = acc_balance;
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

	public String getAccount_number() {
		return account_number;
	}

	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}
	

}
