package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.repository.AddressRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/address", produces = "application/json", consumes = "application/json")
public class AddressAPI
{
	private AddressRepository addressRepository;

	public AddressAPI(AddressRepository addressRepository)
	{
		this.addressRepository = addressRepository;
	}

	@PostMapping
	public Address save(@RequestBody @Validated Address address)
	{
		return null;
	}
}
