package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Attribute;
import com.qrzlo.webshop.data.domain.Dimension;
import com.qrzlo.webshop.data.domain.Variant;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface AttributeRepository extends CrudRepository<Attribute, Long>
{
	Set<Attribute> findAttributesByVariant(Variant variant);
	Set<Attribute> findAttributesByDimension(Dimension dimension);
	void deleteAttributeByVariant(Variant variant);
}
