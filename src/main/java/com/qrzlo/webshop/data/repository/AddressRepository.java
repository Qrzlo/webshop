package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.domain.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address, Long>
{
	List<Address> findAddressesByCustomer(Customer customer);
}
