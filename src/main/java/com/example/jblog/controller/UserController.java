package com.example.jblog.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;

//import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.jblog.domain.OAuthType;
import com.example.jblog.domain.RoleType;
import com.example.jblog.domain.User;
import com.example.jblog.dto.ResponseDTO;
import com.example.jblog.dto.UserDTO;
import com.example.jblog.exception.JBlogException;
import com.example.jblog.persistence.UserRepository;
import com.example.jblog.security.UserDetailsImpl;
import com.example.jblog.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Value("${kakao.default.password}")
	private String kakaoPassword;
	 

	@GetMapping("/user/get/{id}")
	public @ResponseBody User getUser(@PathVariable int id) {
		// 특정 id에 해당하는 User 객체 반환
		// 검색된 회원이 없을 경우 예외 반환
		User findUser = userRepository.findById(id).orElseThrow(new Supplier<JBlogException>() {
			@Override
			public JBlogException get() {
				return new JBlogException(id + "번 회원이 없습니다.");
			}
		});
		return findUser;

	}

	@DeleteMapping("/user/{id}")
	public @ResponseBody ResponseDTO<?> deleteUser(@PathVariable int id, @RequestBody User user) {
		userRepository.deleteById(id);
		return new ResponseDTO<>(HttpStatus.OK.value(), user.getUsername() + " 삭제 성공.");
	}

	@GetMapping("/user/list")
	public @ResponseBody List<User> getUserList() {
		return userRepository.findAll();

	}

	@GetMapping("/user/page")
	public @ResponseBody Page<User> getUserListPaging(
			@PageableDefault(page = 0, size = 2, direction = Sort.Direction.DESC, sort = { "id",
					"username" }) Pageable pageable) {
		// 첫 번째 페이지에 해당하는 2개의 데이터 조회
		// id 내림차순 정렬
		return userRepository.findAll(pageable);

	}


	@Autowired
	private ModelMapper modelMapper;
	
	
	@PostMapping("/auth/insertUser")
	public @ResponseBody ResponseDTO<?> insertUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
		
		//UserDTO -> User 객체로 변환
		User user = modelMapper.map(userDTO, User.class);
		User findUser = userService.getUser(user.getUsername());

		if (findUser.getUsername() == null) {
			userService.insertUser(user);
			return new ResponseDTO<>(HttpStatus.OK.value(), user.getUsername() + " 가입 성공.");
		} else {
			return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), user.getUsername() + "님은 이미 회원입니다.");
		}
	} 

	@GetMapping("/auth/insertUser")
	public String insertUser() {
		return "user/insertUser";
	}

	@PostMapping("/auth/check")
	public @ResponseBody ResponseDTO<?> login(@RequestBody User user) {
		User findUser = userService.getUser(user.getEmail());

		if (findUser.getEmail() == null) {
			return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), "사용 가능한 아이디입니다.");
		} else {
			return new ResponseDTO<>(HttpStatus.OK.value(), "이미 가입된 이메일 주소예요.");
		}
	}
	
	@GetMapping("/auth/login")
	public String login() {
		return "system/login";
	}
	
	@GetMapping("/user/updateUser")
	public String updateUser() {
		return "user/updateUser";
	}
	
	
	
	@PutMapping("/user")
	public @ResponseBody ResponseDTO<?> updateUser(@RequestBody User user, @AuthenticationPrincipal UserDetailsImpl principal) {
		//회원 정보 수정 전, 로그인에 성공한 사용자가 카카오 회원인지 확인
		if(principal.getUser().getOauth().equals(OAuthType.KAKAO) ) {
			//카카오 회원인 경우 비밀번호 고정
			user.setPassword(kakaoPassword);
		}
		
		//회원 정보 수정과 동시에 세션 갱신
		principal.setUser(userService.updateUser(user));
		return new ResponseDTO<>(HttpStatus.OK.value(), user.getUsername() + " 수정 완료");
	}

}
