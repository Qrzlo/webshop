package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.AddressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/address",
		produces = MediaType.APPLICATION_JSON_VALUE,
		consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class AddressAPI
{
	private AddressRepository addressRepository;

	public AddressAPI(AddressRepository addressRepository)
	{
		this.addressRepository = addressRepository;
	}

	@PostMapping
	public ResponseEntity<?> save(@Validated Address address, @AuthenticationPrincipal Customer customer)
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
		catch (ValidationException e)
		{
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
}
