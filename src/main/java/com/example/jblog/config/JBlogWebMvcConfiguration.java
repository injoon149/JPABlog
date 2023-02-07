package com.example.jblog.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JBlogWebMvcConfiguration implements WebMvcConfigurer{
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//registry.addInterceptor(
		//		new AuthenticateInterceptor()).addPathPatterns("/", "/post/**");
		//registry.addInterceptor(localeChangeInterceptor());
	}

}
