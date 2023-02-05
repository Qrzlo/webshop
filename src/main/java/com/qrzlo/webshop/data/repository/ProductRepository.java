package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Category;
import com.qrzlo.webshop.data.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>
{
	Page<Product> findProductsByCategories(Category category, Pageable pageable);
	Page<Product> findProductsByCategoriesIn(Collection<Category> categories, Pageable pageable);
	List<Product> findProductsByNameContainsIgnoreCase(String keyword);
	List<Product> findProductsByNameContainsIgnoreCaseAndCategories(String keyword, Category category);

	@Query("SELECT name FROM Product")
	List<String> getNames();
}
