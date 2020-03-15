package io.sbs.controller;

import io.sbs.dto.AuthenticationDTO;
import io.sbs.dto.AuthenticationOtpDTO;
import io.sbs.service.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {
	
//	@Autowired
//	public AuthenticationService authenticationService;
	
//	UsersRepository users = new UsersRepository();
//	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public void login(@RequestBody AuthenticationDTO authenticationDTO) {
		/*
		 * TODO - Implement this
		 * */
		//Login Successful - 200
		//Login Unsuccessful - 401 Unauthorized error
		//return "Test name";
	}
	
	@RequestMapping(value = "/login_otp", method = RequestMethod.POST)
	public void login_otp(@RequestBody AuthenticationOtpDTO authenticationOtpDTO) {
		//Login Successful - 200
		//Login Unsuccessful - 401 Unauthorized error
		//return "Test name";
		/*
		 * TODO - Implement this
		 * */
	}
}
