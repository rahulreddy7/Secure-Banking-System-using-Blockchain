package io.sbs.security;

public class SecurityConstants {

	public static final String SECRET = "SecretKeyToGenJWTs";
	public static final long EXPIRATION_TIME = 864_000_000; // 10 days
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users/register";
	public static final String LOGIN_URL = "/users/login";
	public static final String OTP_URL = "/users/otp_check";
	public static final String Forgot_Pass = "/users/forgotPass";
	
}
