package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.AddressRepository;
import com.qrzlo.webshop.data.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/address",
		produces = {MediaType.APPLICATION_JSON_VALUE},
		consumes = {MediaType.APPLICATION_JSON_VALUE})
public class AddressAPI
{
	private AddressRepository addressRepository;
	private CustomerRepository customerRepository;

	public AddressAPI(AddressRepository addressRepository, CustomerRepository customerRepository)
	{
		this.addressRepository = addressRepository;
		this.customerRepository = customerRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Address address, @AuthenticationPrincipal Customer customer)
	{
		address.setCustomer(customer);
//		when addresses of Customer is lazy loaded, the @AuthenticationPrincipal customer is fetched by a
//		persistence ctx before this save() is invoked. Then the ctx is closed.
//		customer becomes detached, and when its addresses is accessed, an exception will be thrown
		customer.addAddress(address);
		try
		{
			addressRepository.save(address);
			return ResponseEntity.status(HttpStatus.CREATED).body(address);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@GetMapping
	public ResponseEntity<?> read(@AuthenticationPrincipal Customer customer)
	{
		var addresses = addressRepository.findAddressesByCustomer(customer);
		return ResponseEntity.ok().body(addresses);
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestBody @Validated Address address, @AuthenticationPrincipal Customer customer)
	{
		address.setCustomer(customer);
		boolean success = customer.updateAddress(address);
		addressRepository.save(address);
		return ResponseEntity.ok(success);
	}

	@DeleteMapping
	public ResponseEntity<?> delete(@RequestBody Address address, @AuthenticationPrincipal Customer customer)
	{
		address.setCustomer(null);
		boolean success = customer.deleteAddress(address);
		addressRepository.delete(address);
		return ResponseEntity.ok(success);
	}
}
