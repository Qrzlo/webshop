package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Category;
import com.qrzlo.webshop.data.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>
{
	Page<Product> findProductsByCategories(Category category, Pageable pageable);
	List<Product> findProductsByNameContainsIgnoreCase(String keyword);
	Page<Product> findProductsByNameContainsIgnoreCase(String keyword, Pageable pageable);
	Page<Product> findProductsByNameContainsIgnoreCaseAndCategories(String keyword, Category category, Pageable pageable);
}
