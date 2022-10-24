package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.BasketItem;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.BasketItemRepository;
import com.qrzlo.webshop.data.repository.BasketRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/api/basketitem",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class BasketItemAPI
{
	private BasketItemRepository basketItemRepository;
	private BasketRepository basketRepository;

	public BasketItemAPI(BasketItemRepository basketItemRepository, BasketRepository basketRepository)
	{
		this.basketItemRepository = basketItemRepository;
		this.basketRepository = basketRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated BasketItem basketItem,
									@AuthenticationPrincipal Customer customer)
	{
		try
		{
			var basket = basketRepository.findBasketByCustomer(customer);
			if (!basket.equals(basketItem.getBasket()))
				throw new Exception("corrupted basket id");
			basket.getBasketItems()
					.stream()
					.filter(it -> it.equals(basketItem))
					.findAny()
					.ifPresentOrElse(found -> found.setAmount(found.getAmount() + basketItem.getAmount()),
							() -> basketItemRepository.save(basketItem));
			basket.setLastModified(LocalDateTime.now());
			basketRepository.save(basket);
			return ResponseEntity.ok(basketItem);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

}
