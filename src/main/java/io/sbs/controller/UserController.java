package io.sbs.controller;

import io.sbs.constant.StringConstants;
import io.sbs.constant.UserType;
import io.sbs.dto.AppointmentDTO;
import io.sbs.dto.CustomDTO;
import io.sbs.dto.UserDTO;
import io.sbs.dto.WorkflowDTO;
import io.sbs.model.Account;
import io.sbs.model.LoginOTP;
import io.sbs.model.User;
import io.sbs.security.SecurityConstants;
import io.sbs.service.AppointmentService;
import io.sbs.service.UserService;
import io.sbs.vo.ResultVO;

import io.sbs.exception.RecordNotFoundException;
import io.sbs.service.UserServiceImpl;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    ThreadLocal<Long> startTime = new ThreadLocal<>();
    private Logger logger = LogManager.getLogger();
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();

	@Autowired
	private UserService userService;

	@Autowired
	private AppointmentService appointmentService;
	
	//private Logger logger = LogManager.getLogger();

	// @Autowired
	// private AppointmentService appointmentService;

	@RequestMapping(value = "/homePageDetails", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAccountDetails(HttpServletRequest request) {
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
			String username = JWT
					.require(
							Algorithm.HMAC512(SecurityConstants.SECRET
									.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
					.getSubject();
			return userService.getUserAccountDetails(username);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getUserInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserDetails(@RequestBody User user) {

		try {
			
			String role = null;
			if (userService.getRoleGeneric(user.getUsername()) != null)
				role = userService.getRoleGeneric(user.getUsername()).toString();

			if (role == null)
				return new ResponseEntity<>("No role found.",HttpStatus.BAD_REQUEST);
			
			if (role != UserType.Tier1.toString() || role != UserType.Tier2.toString())
				return new ResponseEntity<>("Insufficient access. ",HttpStatus.UNAUTHORIZED);

			return new ResponseEntity<>(userService.getUserInfo(user
					.getUsername()), HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getUserInfoToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserDetailsToken(HttpServletRequest request) {
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
			String username = JWT
					.require(
							Algorithm.HMAC512(SecurityConstants.SECRET
									.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
					.getSubject();
			return new ResponseEntity<>(userService.getUserInfo(username),
					HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	/*
	 * Function registers the user and saves into user collection
	 */
	@PostMapping("register")
	@ResponseStatus(HttpStatus.CREATED)
	public ResultVO register(@RequestBody CustomDTO customDTO) {
		userService.register(customDTO);
		return ResultVO.createSuccess(customDTO);
	}

	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
		return userService.login(userDTO);
	}

	/*
	 * Function updates the user details and updates them into user collection
	 */

	@RequestMapping(value = "/updateDetails", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResultVO updateDetails(HttpServletRequest request, @RequestBody UserDTO userDTO) {
		
		String token = request.getHeader(SecurityConstants.HEADER_STRING);
		String username = JWT
				.require(
						Algorithm.HMAC512(SecurityConstants.SECRET
								.getBytes())).build()
				.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
				.getSubject();
		userDTO.setUsername(username);
		UserDTO userObj = userService.updateUserInfo(userDTO);
		return ResultVO.createSuccess(userObj);
	}

	@RequestMapping(value = "/appt", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResultVO appt(HttpServletRequest request, @RequestBody AppointmentDTO appointmentDTO) {
		String token = request.getHeader(SecurityConstants.HEADER_STRING);
		String username = JWT
				.require(
						Algorithm.HMAC512(SecurityConstants.SECRET
								.getBytes())).build()
				.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
				.getSubject();
		appointmentDTO.setUsername(username);
		AppointmentDTO apptObj = userService.createAppointment(appointmentDTO);
		return ResultVO.createSuccess(apptObj);
	}

	@PostMapping("approve")
	public ResultVO approve(@RequestBody WorkflowDTO workflow) {
		WorkflowDTO workflowDTO = userService.findWorkflowObj(workflow);
		workflowDTO.setState("Approved");
		WorkflowDTO workflowObj = new WorkflowDTO();
		if (workflowDTO.getType().equals("New_User")) {
			workflowObj = userService.createUser(workflowDTO);
		} else if (workflowDTO.getType().equals("update_details")
				&& workflowDTO.getRole() == UserType.Tier2) {
			workflowObj = userService.updateDetails(workflowDTO);
		} else if (workflowDTO.getType().equals(StringConstants.WORKFLOW_NEW_ACC)) {
			workflowObj = userService.createNewAcc(workflowDTO);
		}
		else if (workflowDTO.getType().equals("appt")
				&& workflowDTO.getRole() == UserType.Tier1) {
			// workflowObj = appointmentService.createAppointments(workflowDTO);
			workflowObj = userService.createAppointments(workflowDTO);
		}
		workflowObj=userService.updateStateOfWorkflow(workflowDTO);
		return ResultVO.createSuccess(workflowObj);
	}
	@PostMapping("decline")
	public void decline(@RequestBody WorkflowDTO workflowDTO) {
		WorkflowDTO workflowObj = new WorkflowDTO();
		workflowObj=userService.deleteWorkflowObj(workflowDTO);
	}

	@PostMapping(path = "/otp_check", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkOTP(HttpServletRequest request,
			@RequestBody LoginOTP login_otp) {
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
			String username = JWT
					.require(
							Algorithm.HMAC512(SecurityConstants.SECRET
									.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
					.getSubject();
			boolean otp_match = userService.checkAndMatchOTP(username,
					login_otp.getOtp());
			UserDTO user_verified = new UserDTO();
			user_verified.setRole(userService.getUserRole(username));
			if (otp_match)
				return new ResponseEntity<>(user_verified,
						HttpStatus.OK);
			else
				return new ResponseEntity<>("OTP Not Verified.",
						HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/forgotPass", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> sendOTPEmail(HttpServletRequest request) {
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
			String username = JWT
					.require(
							Algorithm.HMAC512(SecurityConstants.SECRET
									.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
					.getSubject();
			if (userService.forgotPasswordOTP(username))
				return new ResponseEntity<>("OTP Successfully sent!",
						HttpStatus.OK);
			else
				return new ResponseEntity<>("Error looking up linked email.",
						HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	// todo: verify old password as well
	@RequestMapping(value = "/resetPass", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> resetPassword(@RequestBody UserDTO user) {
		try {
			String username;
			if (user.getUsername() != null) 
				username = user.getUsername();
			else 
				return new ResponseEntity<>("No username found.", HttpStatus.BAD_REQUEST); 
			return userService.resetPass(username,user.getPassword(), user.getNewpassword());
		} catch (Exception e) {
			return new ResponseEntity<>("OK", HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("logout")
	public ResultVO logout(HttpServletRequest request) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        logger.info("user logout time={}s", dateFormat.format(date).toString());

		String token = request.getHeader(SecurityConstants.HEADER_STRING);
		String user = JWT
				.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
				.build()
				.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
				.getSubject();
		return ResultVO.createMsg(user);
	}

	@RequestMapping(value = "/addAcc", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addAcc(HttpServletRequest request,
			@Valid @RequestBody Account acc) {
		try {
			String token = request.getHeader(SecurityConstants.HEADER_STRING);
			String username = JWT
					.require(
							Algorithm.HMAC512(SecurityConstants.SECRET
									.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
					.getSubject();
			return userService.addAccToWorkflow(username, acc);
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/generateCheque", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> generateCheque(HttpServletRequest request, @RequestBody Account acc) {
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

			if (role != UserType.Tier1.toString())
				return new ResponseEntity<>("Insufficient access. ",HttpStatus.UNAUTHORIZED);

			if (Double.isNaN(acc.getAmount_to_debit())|| acc.getAmount_to_debit() <= 0)
				return new ResponseEntity<>("No amount found in request.", HttpStatus.BAD_REQUEST);

			if (acc.getAccount_number() == null || acc.getAccount_number().isEmpty())
				return new ResponseEntity<>("No account number found.", HttpStatus.BAD_REQUEST);

			return userService.generateChequeService(acc);
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping(path = "/creditAmount", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> debitAmt(HttpServletRequest request, @RequestBody Account acc) {
		try {
			
			logger.info("In /debitAmount API controller.");
			
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
			
			if (role != UserType.Tier1.toString())
				return new ResponseEntity<>("Insufficient access. ",HttpStatus.UNAUTHORIZED);

			if (Double.isNaN(acc.getAmount_to_credit())
					|| acc.getAmount_to_credit() <= 0)
				return new ResponseEntity<>("No amount found in request.",HttpStatus.BAD_REQUEST);

			if (acc.getAccount_number() == null || acc.getAccount_number().isEmpty())
				return new ResponseEntity<>("No account number found.", HttpStatus.BAD_REQUEST);

			return userService.creditAmountService(acc);
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

    @RequestMapping(value="/log", method = { RequestMethod.GET, RequestMethod.POST })
    public void log(HttpServletRequest req, Model model, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("this is log download html");

        String filename="logs/info/app.log";
        String downFilename="app.log";//the logs file that need to be download
        response.setContentType("text/plain");
        response.setHeader("Location",downFilename);
        response.setHeader("Content-Disposition", "attachment; filename=" + downFilename);
        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = new FileInputStream( filename);
        byte[] buffer = new byte[1024];
        int i = -1;
        while ((i = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, i);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }
    
    @RequestMapping(value="/workflows",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkflowDTO> getAllWorkflows(HttpServletRequest request){
    	String token = request.getHeader(SecurityConstants.HEADER_STRING);
		String username = JWT
				.require(
						Algorithm.HMAC512(SecurityConstants.SECRET
								.getBytes())).build()
				.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
				.getSubject();
		
		
		return userService.getAllWorkflows(username);
    }

}
