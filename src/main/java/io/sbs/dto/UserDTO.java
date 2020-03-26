package io.sbs.dto;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @date 2020-03-16
 */
public class UserDTO implements Serializable {

   
    private String name;
    private Integer sex; // 1 man 0 woman
    private String username;
    private String password;
    private String email;
	private String address;
	private String newpassword;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
	public String getEmail() {
		return email;
	}
	public void setEmail(String emailString) {
		this.email = emailString;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}
}
