package io.sbs.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.sbs.model.Account;
import io.sbs.model.User;
import io.sbs.service.UserServiceImpl;

@RestController
@RequestMapping(value = "/users")
public class UserController {
	
	UserServiceImpl userService = new UserServiceImpl();

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

}
