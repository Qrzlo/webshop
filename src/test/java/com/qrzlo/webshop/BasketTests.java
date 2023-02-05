package com.qrzlo.webshop;

import com.qrzlo.webshop.data.domain.Basket;
import com.qrzlo.webshop.data.domain.BasketItem;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Inventory;
import com.qrzlo.webshop.data.repository.BasketItemRepository;
import com.qrzlo.webshop.data.repository.BasketRepository;
import com.qrzlo.webshop.data.repository.InventoryRepository;
import com.qrzlo.webshop.service.BasketService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

public class BasketTests
{

	@Test
	public void createNewItemTest()
	{
		Customer customer = new Customer();
		Inventory inventory = new Inventory();
		inventory.setId(1L);

		final int amount = 10;
		BasketItem basketItem = mock(BasketItem.class);
		given(basketItem.getInventory()).willReturn(inventory);
		given(basketItem.getAmount()).willReturn(amount);

		final int initialAmount = 30;
		BasketItem oldItem = mock(BasketItem.class);
		given(oldItem.getInventory()).willReturn(inventory);
		given(oldItem.getAmount()).willReturn(initialAmount);

		Basket basket = mock(Basket.class);
		given(basket.getBasketItems()).willReturn(List.of(oldItem));

		var basketItemRepo = mock(BasketItemRepository.class);
		var basketRepo = mock(BasketRepository.class);
		given(basketRepo.findBasketByCustomer(customer)).willReturn(Optional.of(basket));
		var inventoryRepo = mock(InventoryRepository.class);
		given(inventoryRepo.findById(1L)).willReturn(Optional.of(inventory));
		BasketService basketService = new BasketService(basketItemRepo, basketRepo, inventoryRepo);


		var newBasketItem = basketService.createNewItem(basketItem, customer);


		verify(oldItem).setAmount(initialAmount + amount);
		verify(basketItemRepo).save(oldItem);
		verify(basketRepo).save(basket);
	}

}
