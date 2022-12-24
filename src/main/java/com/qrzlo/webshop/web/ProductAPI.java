package com.qrzlo.webshop.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.qrzlo.webshop.data.Views;
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
		} catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	public class ListOfProductsFiltered
	{
		public ListOfProductsFiltered(int totalPages, Long totalElements, List<Product> content)
		{
			this.totalPages = totalPages;
			this.totalElements = totalElements;
			this.content = content;
		}

		@JsonView(Views.Catalog.class)
		private int totalPages;

		@JsonView(Views.Catalog.class)
		private Long totalElements;

		@JsonView(Views.Catalog.class)
		private List<Product> content;
	}

	@JsonView(Views.Catalog.class)
	@GetMapping
	public ResponseEntity<ListOfProductsFiltered> read(@RequestParam(name = "category", defaultValue = "-1") Integer categoryId,
													   @RequestParam(name = "size", defaultValue = "20") Integer size,
													   @RequestParam(name = "page", defaultValue = "1") Integer page,
													   @RequestParam(name = "order", defaultValue = "id") String prop,
													   @RequestParam(name = "dir", defaultValue = "ASC") String dir)
	{
		dir = dir.toUpperCase();
		page -= 1;
		if (size <= 0 || page < 0 || !Set.of("ASC", "DESC").contains(dir) ||
				!Set.of("forSaleFrom", "name", "id", "defaultPrice").contains(prop))
		{
			return ResponseEntity.badRequest().body(new ListOfProductsFiltered(0, 0L, null));
		}
		Optional<Category> category = categoryRepository.findById(categoryId);
		Sort.Order order = new Sort.Order("ASC".equals(dir) ? Sort.Direction.ASC : Sort.Direction.DESC, prop);
		Sort sort = Sort.by(order);
		Pageable pageable = PageRequest.of(page, size, sort);
		if (category.isEmpty() || category.get().getId() == 1) // no filter. Find according to size and page and orderBy
		{
			var onePage = productRepository.findAll(pageable);
			return ResponseEntity.ok(new ListOfProductsFiltered(onePage.getTotalPages(), onePage.getTotalElements(), onePage.getContent()));
		}
		else // there is a filter on category. currently only category can be filtered, although date could also be
		{
//			var allCategories = categoryRepository.findAll();
//			var filtered = allCategories
//					.stream()
//					.filter(c ->
//					{
//						if (c.equals(category.get()))
//							return true;
//						for (var p = c; p != null; p = p.getParent())
//							if (p.equals(category.get()))
//								return true;
//						return false;
//					})
//					.collect(Collectors.toSet());
//			filtered.forEach(System.out::println);
//			return ResponseEntity.ok(productRepository.findProductsByCategoriesIn(filtered, pageable));
			var onePage = productRepository.findProductsByCategories(category.get(), pageable);
			return ResponseEntity.ok(new ListOfProductsFiltered(onePage.getTotalPages(), onePage.getTotalElements(), onePage.getContent()));
		}
	}

	@JsonView(Views.Product.class)
	@GetMapping("/{id}")
	public ResponseEntity<Product> readOne(@PathVariable Integer id)
	{
		try
		{
			Product product = productRepository.findById(id).orElseThrow();
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


	public class ListOfProductsSearched
	{
		@JsonView(Views.Catalog.class)
		private int totalElements;

		@JsonView(Views.Catalog.class)
		private List<Product> content;

		public ListOfProductsSearched(int totalElements, List<Product> content)
		{
			this.totalElements = totalElements;
			this.content = content;
		}
	}


	@JsonView(Views.Catalog.class)
	@GetMapping(path = "/search")
	public ListOfProductsSearched search(@RequestParam(name = "key") String keyword,
										@RequestParam(name = "category", required = false) Integer categoryId)
	{
		try
		{
			if (categoryId == null)
			{
				var matchedProducts = productRepository.findProductsByNameContainsIgnoreCase(keyword);
				return new ListOfProductsSearched(matchedProducts.size(), matchedProducts);
			}
			var category = categoryRepository.findById(categoryId).orElseThrow();
			var results = productRepository.findProductsByNameContainsIgnoreCaseAndCategories(keyword, category);
			return new ListOfProductsSearched(results.size(), results);
		}
		catch (Exception e)
		{
			return new ListOfProductsSearched(0, List.of());
		}
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
