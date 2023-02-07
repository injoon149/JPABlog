package com.example.jblog.controller.advice;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.jblog.dto.ResponseDTO;

public class ValidationCheckAdvice {
	@Around("execution(* com.example..controller.*Controller.*(..))")
	public Object validationCheck(ProceedingJoinPoint jp) throws Throwable{
		Object[] args = jp.getArgs();
		
		for(Object arg : args) {
			//인자 목록에 BindingResult의 객체가 있다면

		if(arg instanceof BindingResult) {
			BindingResult bindingResult = (BindingResult)arg;
			if(bindingResult.hasErrors()) {
				Map<String, String> errorMap = new HashMap<>();
				//에러 메시지를 Map에 저장한다.
				for(FieldError error : bindingResult.getFieldErrors()) {
					errorMap.put(error.getField(), error.getDefaultMessage());
				}
				return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), errorMap);
			}
			}
		}
		
		return jp.proceed();
	}

}
