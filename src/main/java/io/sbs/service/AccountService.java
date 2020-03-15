package io.sbs.service;

import io.sbs.dto.TransferPostDTO;
import io.sbs.model.Account;

import java.util.List;
import java.util.Map;


public interface AccountService {
	public void transfer_funds(TransferPostDTO transferPostDTO);

	public List<Account> getAllMatchingAccounts(Map<String, ?> query);
}
