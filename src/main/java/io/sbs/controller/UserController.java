package io.sbs.controller;

import io.sbs.dto.AuthenticationDTO;
import io.sbs.dto.UserDTO;
import io.sbs.model.Account;
import io.sbs.model.ApplicationUser;
import io.sbs.service.LoginService;
import io.sbs.service.UserService;
import io.sbs.vo.ResultVO;

import io.sbs.exception.RecordNotFoundException;
import io.sbs.model.Account;
import io.sbs.model.ApplicationUser;
import io.sbs.repository.UserRepository;
import io.sbs.service.UserServiceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;


@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private LoginService loginService;
	
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
			ApplicationUser user = new ApplicationUser();
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
	@ResponseStatus(HttpStatus.CREATED)
	public ResultVO register(@RequestBody UserDTO userDTO) {
		userService.register(userDTO);
		return ResultVO.createSuccess(userDTO);
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
//	@PostMapping("login")
//	public ResultVO login(@RequestBody UserDTO userDTO, HttpSession session) {
//		System.out.println("TEST login");
//		UserDTO userdto=(UserDTO) session.getAttribute("User");
//		if(userdto==null) {
//			userdto = userService.login(userDTO);
//			session.setAttribute("User",(UserDTO)userdto);
//			loginService.addUser(userdto.getUsername());
//			return ResultVO.createSuccess(userdto);
//		}else {
//			if(loginService.loggedInUsers.contains(userdto.getUsername())){
//				return ResultVO.createMsg(userdto);
//			}else {
//				return ResultVO.createSuccess(userdto);
//			}
//		}
////		UserDTO userdto = userService.login(userDTO);
////		return ResultVO.createSuccess(userdto);
//	}
	
	@PostMapping("login")
	public ResultVO login(@RequestBody UserDTO userDTO) {
//		System.out.println("TEST login");
//		UserDTO userdto=(UserDTO) session.getAttribute("User");
//		if(userdto==null) {
		UserDTO userdto = userService.login(userDTO);
//			session.setAttribute("User",(UserDTO)userdto);
		//loginService.addUser(userDTO.getUsername());
		return ResultVO.createSuccess(userDTO);
//		}else {
//			if(loginService.loggedInUsers.contains(userdto.getUsername())){
//				return ResultVO.createMsg(userdto);
//			}else {
//				return ResultVO.createSuccess(userdto);
//			}
		}
//		UserDTO userdto = userService.login(userDTO);
//		return ResultVO.createSuccess(userdto);

	
	
	
	
	
	@GetMapping("logout")
	public String logout(HttpSession session) {
		try {
			if(session.getAttribute("User") !=null  && (session.getAttribute("User") instanceof UserDTO))
			{
				loginService.removeUser(((UserDTO)session.getAttribute("User")).getUsername());
				session.setAttribute("User", null);
				session.invalidate();
			}
			return "redirect:/login";
		}catch (Exception e) {
			return e.toString();
		}
	}

	// @Autowired
	// UserRepository userRepository;

	// UsersRepository users = new UsersRepository();
	//
	// @RequestMapping(value = "/getdata", method = RequestMethod.GET)
	// public User getname() {
	// 	// Test exception
	// 	// return "TEst";
	// 	// TESTING Sample code
	// 	return userRepository.findById("testid").orElseThrow(
	// 			() -> new RecordNotFoundException("Employee id does no exist"));
	// }

}
