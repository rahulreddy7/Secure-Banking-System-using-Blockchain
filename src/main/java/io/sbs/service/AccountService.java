package io.sbs.service;

import io.sbs.dto.TransferPostDTO;
import io.sbs.dto.WorkflowDTO;

public interface AccountService {
	public void transfer_funds(TransferPostDTO transferPostDTO);

	// public List<Account> getAllMatchingAccounts(Map<String, ?> query);

	public WorkflowDTO approveCriticalTransfer(WorkflowDTO workflowDTO);

	public WorkflowDTO approveNonCriticalTransfer(WorkflowDTO workflowDTO);
}
