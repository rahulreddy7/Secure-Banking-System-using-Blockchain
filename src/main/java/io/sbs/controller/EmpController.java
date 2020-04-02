package io.sbs.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import io.sbs.constant.UserType;
import io.sbs.model.Employee;
import io.sbs.security.SecurityConstants;
import io.sbs.service.EmpService;
import io.sbs.service.UserService;

@RestController
@RequestMapping(value = "/emp")
public class EmpController {

	@Autowired
	private EmpService empService;
	
	@Autowired
	private UserService userService;

	@PostMapping(path= "/viewEmp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> viewEmp(HttpServletRequest request, @RequestBody Employee employee){
		try {
			String username;
			if (employee.getUsername() != null) 
				username = employee.getUsername();
			else {
				String token = request.getHeader(SecurityConstants.HEADER_STRING);
		        username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
		                    .build()
		                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
		                    .getSubject();
			}
			return empService.viewEmpService(username);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(path= "/addEmp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addEmp(HttpServletRequest request, @Valid @RequestBody Employee employee){
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
			String username = JWT
					.require(
							Algorithm.HMAC512(SecurityConstants.SECRET
									.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
					.getSubject();

			String role = null;
			if (userService.getRoleGeneric(username) != null)
				role = userService.getRoleGeneric(username).toString();

			if (role == null)
				return new ResponseEntity<>("No role found.",HttpStatus.BAD_REQUEST);
			
			if (role != UserType.Admin.toString())
				return new ResponseEntity<>("Insufficient access.",HttpStatus.UNAUTHORIZED);

			return empService.addNewEmpService(employee, employee.getUsername());
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(path= "/modifyEmp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> modifyEmp(HttpServletRequest request, @RequestBody Employee employee){
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
	        String  username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
	                    .build()
	                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
	                    .getSubject();
	        
	        String role = null;
			if (userService.getRoleGeneric(username) != null)
				role = userService.getRoleGeneric(username).toString();

			if (role == null)
				return new ResponseEntity<>("No role found.",HttpStatus.BAD_REQUEST);
			
			if (role != UserType.Admin.toString())
				return new ResponseEntity<>("Insufficient access. ",HttpStatus.UNAUTHORIZED);
			
			if (employee.getUsername() == null)
				return new ResponseEntity<>("No username found.", HttpStatus.UNAUTHORIZED);
			return empService.modifyEmpService(employee);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	@PostMapping(path= "/deleteEmp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteEmp(HttpServletRequest request, @RequestBody Employee employee){
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
	        String  username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
	                    .build()
	                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
	                    .getSubject();
	        
	        String role = null;
			if (userService.getRoleGeneric(username) != null)
				role = userService.getRoleGeneric(username).toString();

			if (role == null)
				return new ResponseEntity<>("No role found.",HttpStatus.BAD_REQUEST);
			
			if (role != UserType.Admin.toString())
				return new ResponseEntity<>("Insufficient access. ",HttpStatus.UNAUTHORIZED);
	        
			if (employee.getUsername() != null)
				return empService.deleteEmpService(employee, employee.getUsername());
			else
				return new ResponseEntity<>("No username found in body.", HttpStatus.UNAUTHORIZED);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
