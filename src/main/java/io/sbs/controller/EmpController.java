package io.sbs.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.JWTVerificationException;

import io.sbs.model.Employee;
import io.sbs.service.EmpService;

@RestController
@RequestMapping(value = "/emp")
public class EmpController {

	@Autowired
	private EmpService empService;
	
	@PostMapping(path= "/viewEmp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> viewEmp(@RequestBody Employee employee){
		try {
			if (employee.getUsername() == null)
				return new ResponseEntity<>("No username found.", HttpStatus.BAD_REQUEST);
			return empService.viewEmpService(employee);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(path= "/addEmp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addEmp(@Valid @RequestBody Employee employee){
		try {
			return empService.addNewEmpService(employee);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(path= "/modifyEmp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> modifyEmp(@RequestBody Employee employee){
		try {
			if (employee.getUsername() == null)
				return new ResponseEntity<>("No username found.", HttpStatus.BAD_REQUEST);
			return empService.modifyEmpService(employee);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
	@PostMapping(path= "/deleteEmp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteEmp(@RequestBody Employee employee){
		try {
			if (employee.getUsername() == null)
				return new ResponseEntity<>("No username found.", HttpStatus.BAD_REQUEST);
			return empService.deleteEmpService(employee);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
}
