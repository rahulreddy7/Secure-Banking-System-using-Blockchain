package io.sbs.controller;

import io.sbs.dto.TransferPostDTO;
import io.sbs.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/acc")
public class AccountController {

	// AccountsRepository accounts = new AccountsRepository();
	//
	// @RequestMapping(value = "/alldata", method = RequestMethod.GET)
	// public String getAll() {
	// System.out.println("Listing sample data");
	// return accounts.getdata();
	// }
	
	@Autowired 
	AccountService accountService;

	public void transfer_funds(@RequestBody TransferPostDTO transferPostDTO) {
		accountService.transfer_funds(transferPostDTO);
	}
}