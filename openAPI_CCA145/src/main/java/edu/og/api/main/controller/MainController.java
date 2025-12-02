package edu.og.api.main.controller;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@PropertySource("classpath:/config.properties")
@Slf4j
@Controller
public class MainController {

	// code-test temporary url-address
	@GetMapping("/")
	public String mainPage() {
		
		// [todo] 메뉴 선택 따라 포워딩 달라지게
		
		return "main";
	}
		

	@GetMapping("/testCode/testCode") // by a-tag href = "/testCode/testCode"
	public String testCodePage(HttpServletRequest req) {
		
		log.info("requested url: {}", req.getRequestURL().toString()); // requested url: http://localhost:8086/testCode/testCode
		
		// [todo] 메뉴 선택 따라 포워딩 달라지게
		
		return "/testCode/testCode";  // http://localhost:8086/testCode/testCode -> working

	}	
	
}
