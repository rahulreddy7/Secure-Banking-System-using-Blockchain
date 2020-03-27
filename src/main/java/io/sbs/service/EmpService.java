package io.sbs.service;

import org.springframework.http.ResponseEntity;

import io.sbs.model.Employee;

public interface EmpService {

	public ResponseEntity<?> addNewEmpService(Employee employee, String username);

	public ResponseEntity<?> modifyEmpService(Employee employee, String username);

	public ResponseEntity<?> deleteEmpService(Employee employee, String username);

	public ResponseEntity<?> viewEmpService(String username);

}
