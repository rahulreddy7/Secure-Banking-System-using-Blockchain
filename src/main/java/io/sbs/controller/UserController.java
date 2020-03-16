package io.sbs.controller;

import io.sbs.dto.UserDTO;
import io.sbs.model.Account;
import io.sbs.model.User;
import io.sbs.service.UserService;
import io.sbs.vo.ResultVO;
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
	public ResultVO register(@RequestBody UserDTO userDTO) {
		userService.register(userDTO);
		return ResultVO.createSuccess();
	}

	@PostMapping("login")
	public ResultVO login(@RequestBody UserDTO userDTO) {
		UserDTO userdto = userService.login(userDTO);
		return ResultVO.createSuccess(userdto);
	}

}
