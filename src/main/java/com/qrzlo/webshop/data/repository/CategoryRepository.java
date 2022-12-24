package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface CategoryRepository extends CrudRepository<Category, Integer>
{
	Set<Category> findAll();
	Set<Category> findCategoriesByNameContaining(String partialName);
	Set<Category> findCategoriesByParent(Category parent);
}
