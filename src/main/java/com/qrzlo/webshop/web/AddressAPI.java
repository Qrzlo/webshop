package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.service.AddressService;
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
	private AddressService addressService;

	public AddressAPI(AddressService addressService)
	{
		this.addressService = addressService;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Address address, @AuthenticationPrincipal Customer customer)
	{
//		when addresses of Customer is lazy loaded, the @AuthenticationPrincipal customer is fetched by a
//		persistence ctx before repository.save() is invoked. Then the ctx is closed.
//		customer becomes detached, and when its addresses is accessed, an exception will be thrown
		var newAddress = addressService.createAddress(address, customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(newAddress);
	}

	@GetMapping
	public ResponseEntity<?> read(@AuthenticationPrincipal Customer customer)
	{
		var addresses = addressService.readByCustomer(customer);
		return ResponseEntity.ok().body(addresses);
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestBody @Validated Address address, @AuthenticationPrincipal Customer customer)
	{
		var newAddress = addressService.updateAddress(address, customer);
		return ResponseEntity.ok(newAddress);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@AuthenticationPrincipal Customer customer, @PathVariable("id") Integer id)
	{
		var deletedAddress = addressService.deleteAddress(id, customer);
		return ResponseEntity.ok(deletedAddress);
	}
}
