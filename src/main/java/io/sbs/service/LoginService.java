package io.sbs.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class LoginService {
	public static Set<String> loggedInUsers= new HashSet<String>();
	
	public void addUser(String username) {
		if(username != null && !"".equals(username) && !loggedInUsers.contains(username))
		{
			loggedInUsers.add(username);
		}
	}
	
	public void removeUser(String username)
	{
		if(username != null && !"".equals(username) && loggedInUsers.contains(username))
		{
			loggedInUsers.remove(username);
		}
	}
}
