package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Category;
import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.data.repository.CategoryRepository;
import com.qrzlo.webshop.data.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/product",
		produces = {MediaType.APPLICATION_JSON_VALUE},
		consumes = {MediaType.APPLICATION_JSON_VALUE})
public class ProductAPI
{
	private ProductRepository productRepository;
	private CategoryRepository categoryRepository;

	public ProductAPI(ProductRepository productRepository, CategoryRepository categoryRepository)
	{
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Product product)
	{
		try
		{
			var newProduct = productRepository.save(product);
			return ResponseEntity.ok(newProduct);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping
	public ResponseEntity<?> read(@RequestParam(name = "category", defaultValue = "-1") Integer categoryId,
								@RequestParam(name = "size", defaultValue = "20") Integer size,
								@RequestParam(name = "page", defaultValue = "1") Integer page,
								@RequestParam(name = "order", defaultValue = "id") String prop,
								@RequestParam(name = "dir", defaultValue = "ASC") String dir)
	{
		dir = dir.toUpperCase();
		page -= 1;
		if (size <= 0 || page < 0 || !Set.of("ASC", "DESC").contains(dir) ||
			!Set.of("forSaleFrom", "name", "id").contains(prop))
		{
			return ResponseEntity.badRequest().body(null);
		}
		Optional<Category> category = categoryRepository.findById(categoryId);
		Sort.Order order = new Sort.Order("ASC".equals(dir) ? Sort.Direction.ASC : Sort.Direction.DESC, prop);
		Sort sort = Sort.by(order);
		Pageable pageable = PageRequest.of(page, size, sort);
		if (category.isEmpty()) // no filter. Find according to size and page and orderBy
		{
			return ResponseEntity.ok(productRepository.findAll(pageable));
		}
		else // there is a filter on category. currently only category can be filtered, although date could also be
		{
			return ResponseEntity.ok(productRepository.findProductsByCategories(category.get(), pageable));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> readOne(@PathVariable Integer id)
	{
		try
		{
			Product product = productRepository.findById(id).orElseThrow();
			product.getCategories().forEach(System.out::println);
			return ResponseEntity.ok(product);
		}
		catch (NoSuchElementException e)
		{
			return ResponseEntity.badRequest().body(null);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(null);
		}
	}

	@GetMapping(path = "/search")
	public ResponseEntity<List<Product>> search(@RequestParam(name = "key") String keyword)
	{
		return ResponseEntity.ok(productRepository.findProductsByNameContainsIgnoreCase(keyword));
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestBody @Validated Product product)
	{
		try
		{
			var alteredProduct = productRepository.save(product);
			return ResponseEntity.ok(alteredProduct);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

//	@Transactional
	// adding many to many relationships: this worked
	@GetMapping(path = "/relationship/category")
	public ResponseEntity<?> updateCategoryRelationship(@RequestParam(name = "product") Integer productId,
														@RequestParam(name = "category") Integer categoryId)
	{
		try
		{
			Product product = productRepository.findById(productId).orElseThrow();
			Category category = categoryRepository.findById(categoryId).orElseThrow();
			boolean success = product.getCategories().add(category);
			productRepository.save(product);
			return ResponseEntity.ok(success);
		}
		catch (NoSuchElementException e)
		{
			return ResponseEntity.notFound().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}
}
