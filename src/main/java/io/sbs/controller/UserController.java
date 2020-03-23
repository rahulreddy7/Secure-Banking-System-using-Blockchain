package io.sbs.controller;

import io.sbs.dto.UserDTO;
import io.sbs.model.Account;
import io.sbs.model.LoginOTP;
import io.sbs.model.User;
import io.sbs.model.ApplicationUser;
import io.sbs.security.SecurityConstants;
import io.sbs.service.UserService;
import io.sbs.vo.ResultVO;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.impl.JWTParser;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/homePageDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAccountDetails(
			@RequestParam(name = "username", defaultValue = "joliver91") String username) {
		try {
			List<Account> acc_list = new ArrayList<Account>();
			acc_list = userService.getUserAccountDetails(username);
			if (acc_list.size() > 0)
				return new ResponseEntity<>(acc_list, HttpStatus.OK);
			else
				return new ResponseEntity<>("No Records Found!", HttpStatus.NO_CONTENT);		
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getUserInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserDetails(
			@RequestParam(name = "username", defaultValue = "joliver91") String username) {

		try {
			ApplicationUser user = new ApplicationUser();
			user = userService.getUserInfo(username);
			return new ResponseEntity<>(user, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("register")
	/*
	 * Sample payload { "uid":"testuserid", "username":"johnm",
	 * "password":"doe", "sex":1, "name":"testname" }
	 * 
	 * 
	 * Function registers the user and saves into user collection
	 * 
	 * *
	 */
	@ResponseStatus(HttpStatus.CREATED)
	public ResultVO register(@RequestBody UserDTO userDTO) {
		userService.register(userDTO);
		return ResultVO.createSuccess(userDTO);
	}

	/*
	 * Sample payload { "username":"johnm", "password":"doe", }
	 * 
	 * 
	 * Function registers the user and saves into user collection
	 * 
	 * *
	 */

	@PostMapping("login")
	public ResultVO login(@RequestBody UserDTO userDTO) {
		UserDTO userdto = userService.login(userDTO);
		return ResultVO.createSuccess(userDTO);
	}
	
	//needs user name, otp to be checked
	@PostMapping(path= "/otp_check", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkOTP(@RequestBody LoginOTP login_otp) {
		try {
			boolean otp_match = userService.checkAndMatchOTP(login_otp.getUsername(),login_otp.getOtp());
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
	public ResponseEntity<?> sendOTPEmail(@RequestParam(name="username", defaultValue = "joliver91") String username){
		try {
			System.out.println(username);
			if (userService.forgotPasswordOTP(username))
				return new ResponseEntity<>("OTP Successfully sent!", HttpStatus.OK);
			else
				return new ResponseEntity<>("Error looking up linked email.", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("logout")
	public  ResultVO logout(HttpServletRequest request) {
		String token = request.getHeader(SecurityConstants.HEADER_STRING);
        String  user = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
                    .build()
                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getSubject();
		return ResultVO.createMsg(user);
	}

}
