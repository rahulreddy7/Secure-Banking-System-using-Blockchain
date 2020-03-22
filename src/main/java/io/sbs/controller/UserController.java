package io.sbs.controller;

import io.sbs.dto.UserDTO;
import io.sbs.model.Account;
import io.sbs.model.LoginOTP;
import io.sbs.model.User;
import io.sbs.service.UserService;
import io.sbs.vo.ResultVO;

import io.sbs.exception.RecordNotFoundException;
import io.sbs.model.Account;
import io.sbs.model.User;
import io.sbs.repository.UserRepository;
import io.sbs.service.UserServiceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/homePageDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAccountDetails(@RequestParam(name="userid", defaultValue = "joliver91") String userid) {
		try {
			List<Account> acc_list = new ArrayList<Account>();
			acc_list = userService.getUserAccountDetails(userid);
			if (acc_list.size() > 0) 
				return new ResponseEntity<>(acc_list, HttpStatus.OK);
			else
				return new ResponseEntity<>("No Records Found!", HttpStatus.NO_CONTENT);		
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getUserInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserDetails(@RequestParam(name="userid", defaultValue = "joliver91") String userid) {

		try {
			User user = new User();
			user = userService.getUserInfo(userid);
			return new ResponseEntity<>(user, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}


	@PostMapping("register")
	/*
	 * Sample payload
	 * 			{
  				"uid":"testuserid",
  				"username":"johnm",
  				"password":"doe",
  				"sex":1,
  				"name":"testname"
				}
	 * 
	 *
	 * Function registers the user and saves into user collection
	 * 
	 * **/
	public ResultVO register(@RequestBody UserDTO userDTO) {
		userService.register(userDTO);
		return ResultVO.createSuccess();
	}

	/*
	 * Sample payload
	 * 			{
  				"username":"johnm",
  				"password":"doe",
				}
	 * 
	 *
	 * Function registers the user and saves into user collection
	 * 
	 * **/
	@PostMapping("login")
	public ResultVO login(@RequestBody UserDTO userDTO) {
		UserDTO userdto = userService.login(userDTO);
		return ResultVO.createSuccess(userdto);
	}
	
	//needs user name, otp to be checked
	@PostMapping(path= "/otp_check", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkOTP(@RequestBody LoginOTP login_otp) {
		try {
			boolean otp_match = userService.checkAndMatchOTP(login_otp.getUserid(),login_otp.getOtp());
			if (otp_match)
				return new ResponseEntity<>("OTP Verification Successful!", HttpStatus.OK);
			else
				return new ResponseEntity<>("OTP Not Verified.", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/forgotPass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> sendOTPEmail(@RequestParam(name="userid", defaultValue = "joliver91") String userid){
		try {
			System.out.println(userid);
			if (userService.forgotPasswordOTP(userid))
				return new ResponseEntity<>("OTP Successfully sent!", HttpStatus.OK);
			else
				return new ResponseEntity<>("Error looking up linked email.", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
		
	}
}
