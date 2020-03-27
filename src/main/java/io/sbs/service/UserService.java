package io.sbs.service;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import io.sbs.dto.UserDTO;
import io.sbs.dto.WorkflowDTO;
import io.sbs.model.Account;
import io.sbs.model.ApplicationUser;

public interface UserService {

	public List<Account> getUserAccountDetails(String userid);

	public ApplicationUser getUserInfo(String userid);

    void register(UserDTO userDTO);

	UserDTO login(UserDTO userDTO);

	
	public WorkflowDTO updateDetails( WorkflowDTO workflowDTO);


	public boolean checkAndMatchOTP(String userid, String otp);

	public boolean forgotPasswordOTP(String userid);

	public WorkflowDTO createUser(WorkflowDTO workflowDTO);

	UserDTO updateUserInfo(UserDTO user);

}
