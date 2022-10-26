package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.AddressRepository;
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

	public AddressAPI(AddressRepository addressRepository)
	{
		this.addressRepository = addressRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Address address, @AuthenticationPrincipal Customer customer)
	{
		address.setCustomer(customer);
//		when addresses of Customer is lazy loaded, the @AuthenticationPrincipal customer is fetched by a
//		persistence ctx before this save() is invoked. Then the ctx is closed.
//		customer becomes detached, and when its addresses is accessed, an exception will be thrown
		try
		{
			var newAddress = addressRepository.save(address);
			return ResponseEntity.status(HttpStatus.CREATED).body(newAddress);
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
		try
		{
			if (!address.getCustomer().equals(customer))
				throw new Exception("customer id mismatch");
			var oldAddress = addressRepository.findById(address.getId()).orElseThrow();
			if (!oldAddress.getCustomer().equals(customer))
				throw new Exception("customer id mismatch");
			var newAddress = addressRepository.save(address);
			return ResponseEntity.ok(newAddress);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping
	public ResponseEntity<?> delete(@RequestBody @Validated Address address, @AuthenticationPrincipal Customer customer)
	{
		try
		{
			if (!address.getCustomer().equals(customer))
				throw new Exception("customer id mismatch");
			var exist = addressRepository.findById(address.getId()).orElseThrow();
			if (!exist.getCustomer().equals(customer))
				throw new Exception("customer id mismatch");
			addressRepository.delete(exist);
			return ResponseEntity.ok(exist);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}
}
