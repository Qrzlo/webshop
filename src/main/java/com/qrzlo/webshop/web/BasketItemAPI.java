package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.BasketItem;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.BasketItemRepository;
import com.qrzlo.webshop.data.repository.BasketRepository;
import com.qrzlo.webshop.data.repository.InventoryRepository;
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
	private InventoryRepository inventoryRepository;

	public BasketItemAPI(BasketItemRepository basketItemRepository, BasketRepository basketRepository, InventoryRepository inventoryRepository)
	{
		this.basketItemRepository = basketItemRepository;
		this.basketRepository = basketRepository;
		this.inventoryRepository = inventoryRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated BasketItem basketItem,
									@AuthenticationPrincipal Customer customer)
	{
		try
		{
			var basket = basketRepository.findBasketByCustomer(customer);
			if (basketItem.getBasket() != null && !basket.equals(basketItem.getBasket()))
				throw new Exception("corrupted basket id");
			inventoryRepository.findById(basketItem.getInventory().getId()).orElseThrow();
			basket.getBasketItems()
					.stream()
					.filter(it -> it.getInventory().equals(basketItem.getInventory()))
					.findAny()
					.ifPresentOrElse(found ->
						{
							found.setAmount(found.getAmount() + basketItem.getAmount());
							basketItemRepository.save(found);
						},
						() ->
						{
							basketItem.setBasket(basket);
							basketItemRepository.save(basketItem);
						});
			System.out.println("basket item saved");
			basket.setLastModified(LocalDateTime.now());
			basketRepository.save(basket);
			return ResponseEntity.ok(basketItem);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping
	public ResponseEntity<?> read(@RequestParam(name = "basketitem") Long basketItemId)
	{
		try
		{
			var basketItem = basketItemRepository.findById(basketItemId).orElseThrow();
			return ResponseEntity.ok(basketItem);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestBody BasketItem basketItem, @AuthenticationPrincipal Customer customer)
	{
		try
		{
			var basket = basketRepository.findBasketByCustomer(customer);
			if (basket == null)
				throw new Exception("no customer found");
			var index = basket.getBasketItems().indexOf(basketItem);
			if (index == -1)
				throw new Exception("item not found");
			var oldItem = basket.getBasketItems().get(index);
			oldItem.setAmount(basketItem.getAmount());
			var newItem = basketItemRepository.save(oldItem);
			return ResponseEntity.ok(newItem);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping
	public ResponseEntity<?> delete(@RequestBody BasketItem basketItem, @AuthenticationPrincipal Customer customer)
	{
		try
		{
			var basket = basketRepository.findBasketByCustomer(customer);
			var index = basket.getBasketItems().indexOf(basketItem);
			if (index == -1)
				throw new Exception("no basket item matches the deletion");
			basketItemRepository.delete(basket.getBasketItems().get(index));
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}
}
