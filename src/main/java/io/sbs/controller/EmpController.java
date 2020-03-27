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

import io.sbs.model.Employee;
import io.sbs.security.SecurityConstants;
import io.sbs.service.EmpService;

@RestController
@RequestMapping(value = "/emp")
public class EmpController {

	@Autowired
	private EmpService empService;
	
	@PostMapping(path= "/viewEmp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> viewEmp(HttpServletRequest request){
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
	        String  username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
	                    .build()
	                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
	                    .getSubject();
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
	        String  username = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
	                    .build()
	                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
	                    .getSubject();
			return empService.addNewEmpService(employee, username);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
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
			return empService.modifyEmpService(employee, username);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
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
			if (employee.getUsername() != null) username = employee.getUsername();
			return empService.deleteEmpService(employee, username);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
}
