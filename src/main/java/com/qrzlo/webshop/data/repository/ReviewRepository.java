package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.data.domain.Review;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long>
{
	List<Review> findReviewsByProductOrderByCreatedAt(Product product);
	Review findReviewByCustomerAndProduct(Customer customer, Product product);
}
