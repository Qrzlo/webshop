package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Basket;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.BasketRepository;
import com.qrzlo.webshop.data.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;

@RestController
@RequestMapping(path = "/api/customer",
		produces = {MediaType.APPLICATION_JSON_VALUE},
		consumes = {MediaType.APPLICATION_JSON_VALUE})
public class CustomerAPI
{
	private PasswordEncoder passwordEncoder;
	private CustomerRepository customerRepository;
	private BasketRepository basketRepository;

	public CustomerAPI(PasswordEncoder passwordEncoder, CustomerRepository customerRepository, BasketRepository basketRepository)
	{
		this.passwordEncoder = passwordEncoder;
		this.customerRepository = customerRepository;
		this.basketRepository = basketRepository;
	}

	@PostMapping()
	public ResponseEntity<?> create(@RequestBody @Validated Customer customer)
	{
		customer.setPassword(passwordEncoder.encode(customer.getPassword()));
		Basket basket = new Basket();
		basket.setCustomer(customer);
		customer.setBasket(basket);
		try
		{
			Customer created = customerRepository.save(customer);
			basketRepository.save(basket);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		}
		// without the @Validated in param:
		// 1. without the catch: 500 will be returned auto by spring, exception stacktrace in console
		// 2. with the catch: exception catched here. Postman gets the string message specified below
		catch (ValidationException e)
		{
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error message");
		}
	}
}
