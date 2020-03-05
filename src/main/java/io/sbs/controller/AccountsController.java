package io.sbs.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.sbs.dao.AccountsDAO;

@RestController
@RequestMapping(value = "/acc")
public class AccountsController {

	AccountsDAO accounts = new AccountsDAO();
	
	@RequestMapping(value = "/alldata", method = RequestMethod.GET)
	public String getAll() {
		System.out.println("Listing sample data");
		return accounts.getdata();
	}
}