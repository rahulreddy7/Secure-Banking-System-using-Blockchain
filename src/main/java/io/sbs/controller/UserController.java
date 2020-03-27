package io.sbs.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import io.sbs.dto.UserDTO;
import io.sbs.model.Account;
import io.sbs.model.LoginOTP;
import io.sbs.model.User;
import io.sbs.security.SecurityConstants;
import io.sbs.service.UserService;
import io.sbs.vo.ResultVO;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/homePageDetails", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAccountDetails(HttpServletRequest request) {
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
	        String  username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
	                    .build()
	                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
	                    .getSubject();
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

	@RequestMapping(value = "/getUserInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserDetails(@RequestBody User user) {

		try {
			return new ResponseEntity<>(userService.getUserInfo(user.getUsername()), HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getUserInfoToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserDetailsToken(HttpServletRequest request) {
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
	        String  username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
	                    .build()
	                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
	                    .getSubject();
			return new ResponseEntity<>(userService.getUserInfo(username), HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	/*
	 * Sample payload { "uid":"testuserid", "username":"johnm",
	 * "password":"doe", "sex":1, "name":"testname" }
	 * 
	 * 
	 * Function registers the user and saves into user collection
	 * 
	 * *
	 */
	@PostMapping("register")
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
		System.out.println(userDTO.getPassword());
		UserDTO userdto = userService.login(userDTO);
		return ResultVO.createSuccess(userDTO);
	}
	

	/*
	 * Sample payload
	 * 			{
  				"username":"johnm",
  				"address":"doe",
  				"email":"doe11@gmail.com"
				}
	 * 
	 *
	 * Function updates the user details and updates them into user collection
	 * 
	 * **/
	
	@PostMapping("updateDetails")
	public ResultVO updateDetails( @RequestBody UserDTO userDTO) {
		UserDTO userObj = userService.updateDetails(userDTO);
		return ResultVO.createSuccess(userObj);
	}
	
//	@PostMapping("appt")
//	public ResultVO addAppointments(@RequestBody AppointmentDTO appointmentDTO) {
//		AppointmentDTO appointmentdto = appointmentService.createAppointments(appointmentDTO);
//		return ResultVO.createSuccess(appointmentdto);
//	}

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
	
	@RequestMapping(value = "/forgotPass", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> sendOTPEmail(HttpServletRequest request){
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
	        String  username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
	                    .build()
	                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
	                    .getSubject();
			if (userService.forgotPasswordOTP(username))
				return new ResponseEntity<>("OTP Successfully sent!", HttpStatus.OK);
			else
				return new ResponseEntity<>("Error looking up linked email.", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
	
	//todo: verify old password as well
	@RequestMapping(value = "/resetPass", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> resetPassword(HttpServletRequest request, @RequestBody UserDTO user){
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
			String username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();
			return new ResponseEntity<>(userService.resetPass(username, user.getPassword(), user.getNewpassword()), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("OK", HttpStatus.OK);
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
	
	@RequestMapping(value = "/addAcc", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addAcc(HttpServletRequest request,@Valid @RequestBody Account acc){
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
			String username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();
			return userService.addAcc(username, acc);
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	
	}
	
	@RequestMapping(value = "/generateCheque", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> generateCheque(HttpServletRequest request, @RequestBody Account acc){
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
			String username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();
			if (Double.isNaN(acc.getAmount_to_deduct()) || acc.getAmount_to_deduct() <= 0)
				return new ResponseEntity<>("No amount found in request.", HttpStatus.BAD_REQUEST);
			if (acc.getAccount_number() == null || acc.getAccount_number().isEmpty())
				return new ResponseEntity<>("No account number found.", HttpStatus.BAD_REQUEST);
			return userService.generateChequeService(username, acc);
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	
	}
	

	@PostMapping(path= "/debitAmount", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> debitAmt(HttpServletRequest request, @RequestBody Account acc){
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
			String username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();
			if (Double.isNaN(acc.getAmount_to_deduct()) || acc.getAmount_to_deduct() <= 0)
				return new ResponseEntity<>("No amount found in request.", HttpStatus.BAD_REQUEST);
			if (acc.getAccount_number() == null || acc.getAccount_number().isEmpty())
				return new ResponseEntity<>("No account number found.", HttpStatus.BAD_REQUEST);
			return userService.debitAmountService(username, acc);
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

}
