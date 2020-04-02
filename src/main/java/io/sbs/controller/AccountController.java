package io.sbs.controller;

import io.sbs.constant.StringConstants;
import io.sbs.constant.UserType;
import io.sbs.dto.CustomWorkflowDTO;
import io.sbs.dto.TransferOTPPostDTO;
import io.sbs.dto.TransferPostDTO;
import io.sbs.dto.TransferResponseDTO;
import io.sbs.dto.WorkflowDTO;
import io.sbs.security.SecurityConstants;
import io.sbs.service.AccountService;
import io.sbs.service.UserService;
import io.sbs.vo.ResultVO;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@RestController
@RequestMapping(value = "/acc")
public class AccountController {
	@Autowired
	AccountService accountService;

	@Autowired
	UserService userService;

	@RequestMapping(value = "/transfer", method = RequestMethod.POST)
	public TransferResponseDTO transfer_funds(HttpServletRequest request,
			@RequestBody TransferPostDTO transferPostDTO) {
		accountService.transfer_funds(transferPostDTO);
		if (transferPostDTO.getAmount() > 1000.0) {
			return new TransferResponseDTO(true);
		}
		return new TransferResponseDTO(false);
	}

	@RequestMapping(value = "/transfer_otp", method = RequestMethod.POST)
	public ResultVO transfer_funds_otp(HttpServletRequest request,
			@RequestBody TransferOTPPostDTO transferPostDTO) {
		String token = request.getHeader(SecurityConstants.HEADER_STRING);
		String username = JWT
				.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
				.build()
				.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
				.getSubject();
		boolean otp_match = accountService.checkAndMatchOTP(username,
				transferPostDTO.getOtp());
		if (otp_match)
			accountService.transfer_criticalfunds(transferPostDTO);
		else {
			// return failure with message that OTP is not valid
			return ResultVO.createError("OTP is not valid. Please try again");
		}
		return ResultVO.createSuccess();
	}

	@RequestMapping(value = "/transfer_approve", method = RequestMethod.POST)
	public ResultVO transfer_approve(HttpServletRequest request,
			@RequestBody CustomWorkflowDTO workflow) {
		WorkflowDTO workflowObj = null;
		WorkflowDTO workflowDTO = userService.findWorkflowObj(workflow);
		if ((workflowDTO.getType().equals(
				StringConstants.WORKFLOW_CRITICAL_TRANSFER) && workflowDTO
				.getRole() == UserType.Tier2)
				|| (workflowDTO.getType().equals(
						StringConstants.WORKFLOW_NON_CRITICAL_TRANSFER) && workflowDTO
						.getRole() == UserType.Tier1))
			workflowObj = accountService.approveTransfer(workflowDTO);
		workflowObj = userService.updateStateOfWorkflow(workflowDTO);
		workflowObj.setState(StringConstants.WORKFLOW_APPROVED);
		return ResultVO.createSuccess(workflowObj);
	}

	@RequestMapping(value = "/transfer_decline", method = RequestMethod.POST)
	public ResultVO transfer_decline(@RequestBody CustomWorkflowDTO workflow) {
		WorkflowDTO workflowObj = new WorkflowDTO();
		WorkflowDTO workflowDTO = userService.findWorkflowObj(workflow);
		if ((workflowDTO.getType().equals(
				StringConstants.WORKFLOW_CRITICAL_TRANSFER) && workflowDTO
				.getRole() == UserType.Tier2)
				|| (workflowDTO.getType().equals(
						StringConstants.WORKFLOW_NON_CRITICAL_TRANSFER) && workflowDTO
						.getRole() == UserType.Tier1)) {
			// delete the workflow object from the mongo
			userService.deleteWorkflowObj(workflowDTO);
		}
		workflowDTO.setState(StringConstants.WORKFLOW_DECLINED);
		workflowObj = userService.updateStateOfWorkflow(workflowDTO);
		return ResultVO.createSuccess(workflowObj);
	}
}