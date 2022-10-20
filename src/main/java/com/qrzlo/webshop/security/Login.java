package com.qrzlo.webshop.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Login
{
	@GetMapping("/login")
	public String loginPage()
	{
		return "login.html";
	}


}
