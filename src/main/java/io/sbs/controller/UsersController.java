package io.sbs.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.sbs.dao.UsersDAO;

@RestController
@RequestMapping(value = "/users")
public class UsersController {
	
	UsersDAO users = new UsersDAO();
	
	@RequestMapping(value = "/alldata/", method = RequestMethod.GET)
	public String getname() {
		return users.getAll();
	}

}
