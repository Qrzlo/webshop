package com.qrzlo.webshop.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * top level html files need to be remapped here
 */
@Controller
public class StaticResources
{
	@GetMapping("")
	public String home()
	{
		return "home.html";
	}

	@GetMapping("/customer")
	public String customer()
	{
		return "customer.html";
	}

	@GetMapping("/merchant")
	public String merchant()
	{
		return "merchant.html";
	}
}
