package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.domain.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends CrudRepository<Address, Integer>
{
	List<Address> findAddressesByCustomerAndDeleted(Customer customer, boolean deleted);
	Optional<Address> findByIdAndDeleted(Integer id, boolean deleted);
}
