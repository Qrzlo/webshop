package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.data.domain.Variant;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface VariantRepository extends CrudRepository<Variant, Long>
{
	Set<Variant> findVariantsByProduct(Product product);
}
