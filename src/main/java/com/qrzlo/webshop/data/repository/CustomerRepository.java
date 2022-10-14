package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface CustomerRepository extends CrudRepository<Customer, Long>
{
}
