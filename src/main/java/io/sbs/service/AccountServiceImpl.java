package io.sbs.service;

import io.sbs.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	public AccountRepository accountRepository;
	
	public void testMethod() {
		//TODO Implement this
	} 
}