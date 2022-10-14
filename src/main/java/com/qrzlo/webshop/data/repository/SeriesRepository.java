package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.data.domain.Series;
import org.springframework.data.repository.CrudRepository;

public interface SeriesRepository extends CrudRepository<Series, Long>
{
	Series findSeriesByProducts(Product product);
}
