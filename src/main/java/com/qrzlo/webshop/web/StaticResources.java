package com.qrzlo.webshop.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * top level html files need to be remapped here
 */
@Controller
public class StaticResources
{

	@GetMapping(value = {"/",
			"/account",
			"/catalog",
			"/account",
			"/login",
			"/product",
			"/product/{id}",
			"/basket",
			"/checkout"})
	public String indexByTopPaths(@PathVariable(required = false) String id)
	{
		return "index.html";
	}
}
