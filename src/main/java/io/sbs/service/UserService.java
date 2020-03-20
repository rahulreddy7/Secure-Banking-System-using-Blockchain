package io.sbs.service;

import java.util.List;

import io.sbs.dto.UserDTO;
import io.sbs.model.Account;
import io.sbs.model.User;

public interface UserService {

	public List<Account> getUserAccountDetails(String userid);

	public User getUserInfo(String userid);

    void register(UserDTO userDTO);

	UserDTO login(UserDTO userDTO);
}
