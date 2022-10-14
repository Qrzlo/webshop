package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Purchase;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseRepository extends CrudRepository<Purchase, Long>
{
	List<Purchase> findPurchasesByCustomerOrderByCreatedAt(Customer customer);
	List<Purchase> findPurchasesByCustomerAndCreatedAtIsBetweenOrderByCreatedAt(Customer customer, LocalDateTime from, LocalDateTime to);
}
