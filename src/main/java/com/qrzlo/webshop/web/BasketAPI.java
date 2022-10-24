package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.BasketRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/basket",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class BasketAPI
{
	private BasketRepository basketRepository;

	public BasketAPI(BasketRepository basketRepository)
	{
		this.basketRepository = basketRepository;
	}

	@GetMapping
	public ResponseEntity<?> read(@AuthenticationPrincipal Customer customer)
	{
		try
		{
			var basket = basketRepository.findBasketByCustomer(customer);
			basket.updatePrice();
			return ResponseEntity.ok(basket);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}
}
