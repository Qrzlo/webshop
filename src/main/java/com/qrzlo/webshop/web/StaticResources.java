package com.qrzlo.webshop.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * top level html files need to be remapped here
 */
@Controller
public class StaticResources
{

	@GetMapping({"/",
			"/account",
			"/catalog",
			"/product/**",
			"/account",
			"/login",
			"/basket",
			"/checkout"})
	public String index()
	{
		return "index.html";
	}

}
