package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Dimension;
import com.qrzlo.webshop.data.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DimensionRepository extends CrudRepository<Dimension, Integer>
{
	List<Dimension> findDimensionsByProduct(Product product);
}
