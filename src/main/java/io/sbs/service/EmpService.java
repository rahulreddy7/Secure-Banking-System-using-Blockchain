package io.sbs.service;

import org.springframework.http.ResponseEntity;

import io.sbs.model.Employee;

public interface EmpService {

	public ResponseEntity<?> addNewEmpService(Employee employee);

	public ResponseEntity<?> modifyEmpService(Employee employee);

	public ResponseEntity<?> deleteEmpService(Employee employee);

	public ResponseEntity<?> viewEmpService(Employee employee);

}
