package io.sbs.service;

import java.util.List;

import io.sbs.model.Account;
import io.sbs.model.User;

interface UserService {

	public List<Account> getUserAccountDetails(String userid);

	public User getUserInfo(String userid);
}
