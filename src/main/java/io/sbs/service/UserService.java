package io.sbs.service;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import io.sbs.dto.UserDTO;
import io.sbs.model.Account;
import io.sbs.model.ApplicationUser;

public interface UserService {

	public List<Account> getUserAccountDetails(String userid);

	public ApplicationUser getUserInfo(String userid);

    void register(UserDTO userDTO);

	UserDTO login(UserDTO userDTO);

	
	public UserDTO updateDetails( UserDTO user);


	public boolean checkAndMatchOTP(String userid, String otp);

	public boolean forgotPasswordOTP(String userid);

}
