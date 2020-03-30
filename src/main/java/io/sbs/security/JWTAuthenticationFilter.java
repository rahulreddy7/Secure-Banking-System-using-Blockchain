package io.sbs.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.mongodb.client.model.Filters.eq;

import io.sbs.model.ApplicationUser;
import io.sbs.model.LoginOTP;
import io.sbs.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.User;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

//public class JWTAuthenticationFilter extends
//		UsernamePasswordAuthenticationFilter{
//
//	private AuthenticationManager authenticationManager;
//	
//
//public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
//		super();
//		setFilterProcessesUrl("/users/login");
//		this.authenticationManager = authenticationManager;
//	}
//
////	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
////		this.authenticationManager = authenticationManager;
////		setFilterProcessesUrl("/users/login");
////		// TODO Auto-generated constructor stub
////	}
//
//	@Override
//	public Authentication attemptAuthentication(HttpServletRequest req,
//			HttpServletResponse res) throws AuthenticationException {
//		try {
//			ApplicationUser creds = new ObjectMapper().readValue(req.getInputStream(),
//					ApplicationUser.class);
//			return authenticationManager
//					.authenticate(new UsernamePasswordAuthenticationToken(creds
//							.getUsername(), creds.getPassword(),
//							new ArrayList<>()));
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	@Override
//	protected void successfulAuthentication(HttpServletRequest req,
//			HttpServletResponse res, FilterChain chain, Authentication auth)
//			throws IOException, ServletException {
//
//		String token = JWT
//				.create()
//				.withSubject(((User) auth.getPrincipal()).getUsername())
//				.withExpiresAt(
//						new Date(System.currentTimeMillis()
//								+ SecurityConstants.EXPIRATION_TIME))
//				.sign(HMAC512(SecurityConstants.SECRET.getBytes()));
//		res.addHeader(SecurityConstants.HEADER_STRING,
//				SecurityConstants.TOKEN_PREFIX + token);
//	}
//
//}

public class JWTAuthenticationFilter extends
UsernamePasswordAuthenticationFilter{

private AuthenticationManager authenticationManager;

@Autowired
private UserService userService;

public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
super();
setFilterProcessesUrl("/users/otp_check");
this.authenticationManager = authenticationManager;
}

@Override
public Authentication attemptAuthentication(HttpServletRequest req,
	HttpServletResponse res) throws AuthenticationException {
try {
	LoginOTP creds = new ObjectMapper().readValue(req.getInputStream(),
			LoginOTP.class);
	return authenticationManager
			.authenticate(new UsernamePasswordAuthenticationToken(creds
					.getUsername(), creds.getOtp(),
					new ArrayList<>()));
} catch (IOException e) {
	throw new RuntimeException(e);
}
}

@Override
protected void successfulAuthentication(HttpServletRequest req,
	HttpServletResponse res, FilterChain chain, Authentication auth)
	throws IOException, ServletException {
	String role = userService.getUserRole("");

String token = JWT
		.create()
		.withSubject(((User) auth.getPrincipal()).getUsername())
		.withExpiresAt(
				new Date(System.currentTimeMillis()
						+ SecurityConstants.EXPIRATION_TIME))
		.sign(HMAC512(SecurityConstants.SECRET.getBytes()));
String username = JWT.require(
		Algorithm.HMAC512(SecurityConstants.SECRET
				.getBytes())).build().verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();
res.addHeader(SecurityConstants.HEADER_STRING,
		SecurityConstants.TOKEN_PREFIX + token);
//System.out.println("HEREE E " + username);
//if (role != null)
//	res.addHeader("userRole", role);
//else
//	res.addHeader("userRole", "norole");
	

}

public String getUserRole(String username) {
	try {
		final MongoClient mongoClient = MongoClients.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
		final MongoDatabase database = mongoClient.getDatabase("mydb");
		MongoCollection<Document> collection = database.getCollection("user");
		Document myDoc = collection.find(eq("username", username)).first();
		if (myDoc == null)
			return null;
		String role = myDoc.get("role").toString();
		if (role != null)
			return role;
		else
			return null;
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
}
}