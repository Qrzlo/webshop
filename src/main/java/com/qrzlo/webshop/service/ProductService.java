package com.qrzlo.webshop.service;

import com.qrzlo.webshop.data.domain.Category;
import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.data.repository.CategoryRepository;
import com.qrzlo.webshop.data.repository.ProductRepository;
import com.qrzlo.webshop.util.exception.AbsentDataException;
import com.qrzlo.webshop.util.exception.InvalidRequestException;
import com.qrzlo.webshop.web.ProductAPI;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService
{
	private ProductRepository productRepository;
	private CategoryRepository categoryRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository)
	{
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	public Product create(Product product)
	{
		return productRepository.save(product);
	}

	public ProductAPI.ListOfProductsFiltered filter(Integer categoryId,
													Integer size,
													Integer page,
													String prop,
													String dir)
	{
		dir = dir.toUpperCase();
		page -= 1;
		if (size <= 0 || page < 0 || !Set.of("ASC", "DESC").contains(dir) ||
				!Set.of("forSaleFrom", "name", "id", "defaultPrice").contains(prop))
		{
			throw new InvalidRequestException("The request parameters are invalid");
		}
		Optional<Category> category = categoryRepository.findById(categoryId);
		Sort.Order order = new Sort.Order("ASC".equals(dir) ? Sort.Direction.ASC : Sort.Direction.DESC, prop);
		Sort sort = Sort.by(order);
		Pageable pageable = PageRequest.of(page, size, sort);
		if (category.isEmpty() || category.get().getId() == 1) // no filter. Find according to size and page and orderBy
		{
			var onePage = productRepository.findAll(pageable);
			return new ProductAPI.ListOfProductsFiltered(onePage.getTotalPages(), onePage.getTotalElements(), onePage.getContent());
		}
		else // there is a filter on category
		{
			var onePage = productRepository.findProductsByCategories(category.get(), pageable);
			return new ProductAPI.ListOfProductsFiltered(onePage.getTotalPages(), onePage.getTotalElements(), onePage.getContent());
		}
	}

	public boolean updateCategoryRelationship(Integer productId, Integer categoryId)
	{
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new AbsentDataException("The product cannot be found"));
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new AbsentDataException("The category cannot be found"));
		boolean success = product.getCategories().add(category);
		productRepository.save(product);
		return success;
	}

	public ProductAPI.ListOfProductsSearched search(String keyword, Integer categoryId)
	{
		if (categoryId == null)
		{
			var matchedProducts = productRepository.findProductsByNameContainsIgnoreCase(keyword);
			return new ProductAPI.ListOfProductsSearched(matchedProducts.size(), matchedProducts);
		}
		var category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new AbsentDataException("The category cannot be found"));
		var results = productRepository.findProductsByNameContainsIgnoreCaseAndCategories(keyword, category);
		return new ProductAPI.ListOfProductsSearched(results.size(), results);
	}

	public Product readOne(Integer id)
	{
		return productRepository.findById(id).orElseThrow(() -> new AbsentDataException("The product cannot be found"));
	}

}
