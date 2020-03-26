package io.sbs.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import io.sbs.model.ApplicationUser;
import io.sbs.model.LoginOTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.User;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;

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

String token = JWT
		.create()
		.withSubject(((User) auth.getPrincipal()).getUsername())
		.withExpiresAt(
				new Date(System.currentTimeMillis()
						+ SecurityConstants.EXPIRATION_TIME))
		.sign(HMAC512(SecurityConstants.SECRET.getBytes()));
res.addHeader(SecurityConstants.HEADER_STRING,
		SecurityConstants.TOKEN_PREFIX + token);
}

}