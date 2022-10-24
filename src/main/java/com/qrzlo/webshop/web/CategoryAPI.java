package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Category;
import com.qrzlo.webshop.data.repository.CategoryRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/category",
				produces = {MediaType.APPLICATION_JSON_VALUE},
				consumes = {MediaType.APPLICATION_JSON_VALUE})
public class CategoryAPI
{
	private CategoryRepository categoryRepository;

	public CategoryAPI(CategoryRepository categoryRepository)
	{
		this.categoryRepository = categoryRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Category category)
	{
		try
		{
			categoryRepository.save(category);
			return ResponseEntity.ok(category);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.badRequest().body(null);
		}
	}
}
