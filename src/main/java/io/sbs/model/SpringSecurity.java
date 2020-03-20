package io.sbs.model;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity 
public class SpringSecurity extends WebSecurityConfigurerAdapter {
	@Override
  public void configure (WebSecurity web) throws Exception {
//    http
//      .httpBasic()
//    .and()
    	web.ignoring().antMatchers("/users/**");
//      .authorizeRequests()
//        .antMatchers("/users/register/","/users/login/","/users/logout/").permitAll()
//        .anyRequest().authenticated();
//      .and().csrf()
//      .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
  }
}