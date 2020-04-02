package io.sbs.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.sbs.constant.UserType;
import io.sbs.dto.AppointmentDTO;
import io.sbs.dto.CustomDTO;
import io.sbs.dto.UserDTO;
import io.sbs.dto.WorkflowDTO;
import io.sbs.model.Account;
import io.sbs.model.User;

public interface UserService {

	public ResponseEntity<?> getUserAccountDetails(String userid);

	public User getUserInfo(String userid);

    void register(CustomDTO customDTO);

	ResponseEntity<?> login(UserDTO userDTO);
	
	public WorkflowDTO updateDetails( WorkflowDTO workflowDTO);

	public boolean checkAndMatchOTP(String userid, String otp);

	public boolean forgotPasswordOTP(String userid);


	public WorkflowDTO createUser(WorkflowDTO workflowDTO);

	UserDTO updateUserInfo(UserDTO user);


	public ResponseEntity<?> resetPass(String username, String oldpassword, String newpassword);

	public ResponseEntity<?> addAccToWorkflow(String username, Account acc);

	public ResponseEntity<?> generateChequeService(Account acc);

	public ResponseEntity<?> creditAmountService(Account acc);

	public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO);

	WorkflowDTO createAppointments(WorkflowDTO workflowDTO);

	public WorkflowDTO updateStateOfWorkflow(WorkflowDTO workflowDTO);

	public UserType getUserRole(String username);

	public WorkflowDTO createNewAcc(WorkflowDTO workflowDTO);
  
	public WorkflowDTO deleteWorkflowObj(WorkflowDTO workflowDTO);
	
	public List<WorkflowDTO> getAllWorkflows(String username);

	public WorkflowDTO findWorkflowObj(WorkflowDTO workflow_id);

	public UserType getRoleGeneric(String username);

	public ResponseEntity<?> deleteAccService(Account acc);

}
