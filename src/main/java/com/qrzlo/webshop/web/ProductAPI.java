package com.qrzlo.webshop.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.qrzlo.webshop.data.Views;
import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/product",
		produces = {MediaType.APPLICATION_JSON_VALUE},
		consumes = {MediaType.APPLICATION_JSON_VALUE})
public class ProductAPI
{
	private ProductService productService;

	public ProductAPI(ProductService productService)
	{
		this.productService = productService;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Product product)
	{
		return ResponseEntity.ok(productService.create(product));
	}

	public static class ListOfProductsFiltered
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
	public ResponseEntity<ListOfProductsFiltered> filter(@RequestParam(name = "category", defaultValue = "-1") Integer categoryId,
													   @RequestParam(name = "size", defaultValue = "20") Integer size,
													   @RequestParam(name = "page", defaultValue = "1") Integer page,
													   @RequestParam(name = "order", defaultValue = "id") String prop,
													   @RequestParam(name = "dir", defaultValue = "ASC") String dir)
	{
		var results = productService.filter(categoryId, size, page, prop, dir);
		return ResponseEntity.ok(results);
	}

	@JsonView(Views.Product.class)
	@GetMapping("/{id}")
	public ResponseEntity<Product> readOne(@PathVariable Integer id)
	{
		return ResponseEntity.ok(productService.readOne(id));
	}


	public static class ListOfProductsSearched
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
		return productService.search(keyword, categoryId);
	}

	// adding many to many relationships: this worked
	@GetMapping(path = "/relationship/category")
	public ResponseEntity<?> updateCategoryRelationship(@RequestParam(name = "product") Integer productId,
														@RequestParam(name = "category") Integer categoryId)
	{
		var success = productService.updateCategoryRelationship(productId, categoryId);
		return ResponseEntity.ok(success);
	}

}
