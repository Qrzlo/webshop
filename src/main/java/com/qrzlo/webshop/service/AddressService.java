package com.qrzlo.webshop.service;

import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.AddressRepository;
import com.qrzlo.webshop.util.exception.CorruptedDataException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService
{
	private AddressRepository addressRepository;

	public AddressService(AddressRepository addressRepository)
	{
		this.addressRepository = addressRepository;
	}

	public Address createAddress(Address address, Customer customer)
	{
		address.setCustomer(customer);
		address.setDeleted(false);
		var newAddress = addressRepository.save(address);
		return newAddress;
	}

	public List<Address> readByCustomer(Customer customer)
	{
		return addressRepository.findAddressesByCustomerAndDeleted(customer, false);
	}

	public Address updateAddress(Address address, Customer customer)
	{
		var oldAddress = addressRepository.findByIdAndDeleted(address.getId(), false).orElseThrow();
		if (!oldAddress.getCustomer().equals(customer))
			throw new CorruptedDataException("customer id mismatch");
		address.setCustomer(customer);
		return addressRepository.save(address);
	}

	public Address deleteAddress(Integer id, Customer customer)
	{
		var address = addressRepository.findByIdAndDeleted(id, false).orElseThrow();
		if (!address.getCustomer().equals(customer))
			throw new CorruptedDataException("customer id mismatch");
		address.setDeleted(true);
		addressRepository.save(address);
		return address;
	}
}
