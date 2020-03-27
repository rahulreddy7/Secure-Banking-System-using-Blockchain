package io.sbs.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.sbs.dto.UserDTO;
import io.sbs.model.Account;
import io.sbs.model.User;

public interface UserService {

	public List<Account> getUserAccountDetails(String userid);

	public User getUserInfo(String userid);

    void register(UserDTO userDTO);

	UserDTO login(UserDTO userDTO);
	
	public UserDTO updateDetails( UserDTO user);

	public boolean checkAndMatchOTP(String userid, String otp);

	public boolean forgotPasswordOTP(String userid);


	public ResponseEntity<?> resetPass(String username, String oldpassword, String newpassword);

	public ResponseEntity<?> addAcc(String username, Account acc);

	public ResponseEntity<?> generateChequeService(String username, Account acc);

	public ResponseEntity<?> debitAmountService(String username, Account acc);

}
