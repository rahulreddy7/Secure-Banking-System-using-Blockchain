package io.sbs.security;

import io.sbs.service.OtpServiceImpl;
import io.sbs.service.UserDetailsServiceImpl;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	//private UserDetailsServiceImpl userDetailsService;
	
	private OtpServiceImpl otpService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

//	public WebSecurity(UserDetailsServiceImpl userDetailsService) {
//		this.userDetailsService = userDetailsService;
//	}
	
	public WebSecurity(OtpServiceImpl otpService) {
		this.otpService=otpService;
		// TODO Auto-generated constructor stub
	}

//	@Override
//	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
//	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		auth.userDetailsService(otpService).passwordEncoder(bCryptPasswordEncoder);
	}
		

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors()
				.and()
				.csrf()
				.disable()
				.authorizeRequests()
				.antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL,
						SecurityConstants.LOGIN_URL,SecurityConstants.OTP_URL,SecurityConstants.Forgot_Pass,SecurityConstants.RESET_PASS)
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(
						(req, resp, e) -> resp
								.sendError(HttpServletResponse.SC_UNAUTHORIZED))
				.and()
				//.addFilterAfter(new JWTAuthenticationFilter(authenticationManager()), JWTAuthenticationFilter.class)
				.addFilter(new JWTAuthenticationFilter(authenticationManager()))
				.addFilter(new JWTAuthorizationFilter(authenticationManager()))
				// this disables session creation on Spring Security
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.applyPermitDefaultValues();
		corsConfig.addExposedHeader(SecurityConstants.HEADER_STRING);
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}

}
