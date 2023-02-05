package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Category;
import com.qrzlo.webshop.data.repository.CategoryRepository;
import com.qrzlo.webshop.util.exception.AbsentDataException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

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
		var newCategory = categoryRepository.save(category);
		return ResponseEntity.ok(newCategory);
	}

	@GetMapping
	public ResponseEntity<?> getOne(@RequestParam("id") Integer id)
	{
		var found = categoryRepository
				.findById(id)
				.orElseThrow(() -> new AbsentDataException("The category cannot be found"));
		return ResponseEntity.ok(found);
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAll()
	{
		var all = categoryRepository.findAll();
		var top = all.stream().filter(a -> a.getParent() == null).collect(Collectors.toSet());
		return ResponseEntity.ok(top);
	}
}
