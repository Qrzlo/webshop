package com.qrzlo.webshop.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.qrzlo.webshop.data.Views;
import com.qrzlo.webshop.data.domain.BasketItem;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.service.BasketService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/basketitem",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class BasketItemAPI
{

	private BasketService basketService;

	public BasketItemAPI(BasketService basketService)
	{
		this.basketService = basketService;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated BasketItem basketItem,
									@AuthenticationPrincipal Customer customer)
	{
		var newItem = basketService.createNewItem(basketItem, customer);
		return ResponseEntity.ok(newItem);
	}

	@JsonView(Views.Basket.class)
	@PutMapping
	public ResponseEntity<?> update(@RequestBody BasketItem basketItem, @AuthenticationPrincipal Customer customer)
	{
		var newItem = basketService.updateOldItem(basketItem, customer);
		return ResponseEntity.ok(newItem);
	}

	@DeleteMapping
	public ResponseEntity<?> delete(@RequestBody BasketItem basketItem, @AuthenticationPrincipal Customer customer)
	{
		basketService.deleteItem(basketItem, customer);
		return ResponseEntity.ok().build();
	}
}
