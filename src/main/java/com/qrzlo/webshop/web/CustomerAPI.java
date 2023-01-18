package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Basket;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.BasketRepository;
import com.qrzlo.webshop.data.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
		Customer created = customerRepository.save(customer);
		basketRepository.save(basket);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping("/session")
	public ResponseEntity<?> login(@AuthenticationPrincipal Customer customer)
	{
		return ResponseEntity.ok(customer);
	}
}
