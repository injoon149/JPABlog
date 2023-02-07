package com.example.jblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import com.example.jblog.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JBlogWebSecurityConfiguration{
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	//등록된 AuthenticationManager을 불러오기 위한 Bean
	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	     return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//사용자가 입력한 username으로 User 객체를 검색하고 password를 비교한다.
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	@Order(SecurityProperties.BASIC_AUTH_ORDER)
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/webjars/**", "/js/**", "/image/**", "/", "/auth/**", "/oauth/**").permitAll();
		
		//나머지 경로는 인증이 필요하다.
		http.authorizeRequests().anyRequest().authenticated();
		
		//CSRF 토큰을 받지 않음
		http.csrf().disable();
		
		//사용자 정의 로그인 화면 제공
		http.formLogin().loginPage("/auth/login");
		
		//로그인 요청 URI를 변경한다.
		http.formLogin().loginProcessingUrl("/auth/securitylogin");
		
		//로그아웃 설정
		http.logout().logoutUrl("/auth/logout").logoutSuccessUrl("/");		
		return http.build();
		
	}

}
