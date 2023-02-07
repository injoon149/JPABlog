package com.example.jblog.service;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jblog.domain.OAuthType;
import com.example.jblog.domain.RoleType;
import com.example.jblog.domain.User;
import com.example.jblog.persistence.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional(readOnly = true)
	public User getUserByEmail(String useremail) {
		//검색 결과가 없을 때 빈 User 객체 반환
		User findUser = userRepository.findByEmail(useremail).orElseGet(
				new Supplier<User>() {
					@Override
					public User get() {
						return new User();
					}
				});
		
		return findUser;
	} 
	
	@Transactional(readOnly = true)
	public User getUser(String username) {
		//검색 결과가 없을 때 빈 객체 반환(람다식)
		User findUser = userRepository.findByUsername(username).orElseGet(()-> {
			return new User();
		});
		
		return findUser;
	}
	
	@Transactional
	public void insertUser(User user) {
		//비밀번호를 암호화하여 설정한다.
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		user.setRole(RoleType.USER);
		if(user.getOauth() == null) {
			user.setOauth(OAuthType.JBLOG);
		}
		userRepository.save(user);
	}
	
	@Transactional
	public User updateUser(User user) {
		User findUser = userRepository.findById(user.getId()).get();
		findUser.setUsername(user.getUsername());
		findUser.setPassword(passwordEncoder.encode(user.getPassword()));
		findUser.setEmail(user.getEmail());
		
		return findUser;
	}
	
	

}
