package com.qrzlo.webshop.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.BasketRepository;
import com.qrzlo.webshop.service.BasketService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qrzlo.webshop.data.Views;

@RestController
@RequestMapping(path = "/api/basket",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class BasketAPI
{
	private BasketService basketService;

	public BasketAPI(BasketService basketService)
	{
		this.basketService = basketService;
	}

	@JsonView(Views.Basket.class)
	@GetMapping
	public ResponseEntity<?> read(@AuthenticationPrincipal Customer customer)
	{
		var basket = basketService.getBasket(customer);
		basket.updatePrice();
		return ResponseEntity.ok(basket);
	}
}
