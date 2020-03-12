package io.sbs.service;

import io.sbs.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;

public class AccountServiceImpl implements AccountService {
	
	@Autowired
	public AccountRepository accountRepository;
	
	public void testMethod() {
		//TODO Implement this
	} 
}
