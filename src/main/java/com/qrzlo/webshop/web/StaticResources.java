package com.qrzlo.webshop.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StaticResources
{
	@RequestMapping("")
	public String home()
	{
		return "home.html";
	}
}
