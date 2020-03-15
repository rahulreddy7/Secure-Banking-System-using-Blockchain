package io.sbs.controller;

import io.sbs.exception.RecordNotFoundException;
import io.sbs.model.User;
import io.sbs.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	UserRepository userRepository;

	// UsersRepository users = new UsersRepository();
	//
	@RequestMapping(value = "/getdata", method = RequestMethod.GET)
	public User getname() {
		// Test exception
		// return "TEst";
		// TESTING Sample code
		return userRepository.findById("testid").orElseThrow(
				() -> new RecordNotFoundException("Employee id does no exist"));
	}
}
