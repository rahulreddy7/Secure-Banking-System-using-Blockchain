package io.sbs.service;

import io.sbs.dto.TransferOTPPostDTO;
import io.sbs.dto.TransferPostDTO;
import io.sbs.dto.WorkflowDTO;

public interface AccountService {
	public void transfer_funds(TransferPostDTO transferPostDTO);
	
	public boolean checkAndMatchOTP(String username, String otp);
	
	public void transfer_criticalfunds(TransferOTPPostDTO transferPostDTO);
	
	public WorkflowDTO approveTransfer(WorkflowDTO workflowDTO);
}
