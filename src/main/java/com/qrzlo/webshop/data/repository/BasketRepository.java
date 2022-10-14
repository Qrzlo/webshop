package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Basket;
import com.qrzlo.webshop.data.domain.Customer;
import org.springframework.data.repository.CrudRepository;

public interface BasketRepository extends CrudRepository<Basket, Long>
{
	Basket findBasketByCustomer(Customer customer);
}
