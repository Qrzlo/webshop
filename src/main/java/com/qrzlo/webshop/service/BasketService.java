package com.qrzlo.webshop.service;

import com.qrzlo.webshop.data.domain.Basket;
import com.qrzlo.webshop.data.domain.BasketItem;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.BasketItemRepository;
import com.qrzlo.webshop.data.repository.BasketRepository;
import com.qrzlo.webshop.data.repository.InventoryRepository;
import com.qrzlo.webshop.util.exception.AbsentDataException;
import com.qrzlo.webshop.util.exception.CorruptedDataException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BasketService
{
	private BasketItemRepository basketItemRepository;
	private BasketRepository basketRepository;
	private InventoryRepository inventoryRepository;

	public BasketService(BasketItemRepository basketItemRepository, BasketRepository basketRepository, InventoryRepository inventoryRepository)
	{
		this.basketItemRepository = basketItemRepository;
		this.basketRepository = basketRepository;
		this.inventoryRepository = inventoryRepository;
	}

	public Basket getBasket(Customer customer)
	{
		return basketRepository.findBasketByCustomer(customer).orElseThrow(() -> new AbsentDataException("No such basket found"));
	}

	/**
	 * A helper method that updates the last-modified field {@code lastModified}
	 * @param basket
	 */
	private void updateBasket(Basket basket)
	{
		basket.setLastModified(LocalDateTime.now());
	}

	/**
	 * Create a new basket item: if there is one with the same inventory id, the new amount will be added;
	 * otherwise a new item is saved.
	 * @param basketItem the basket item to be saved or updated
	 * @param customer the owner of the basket
	 * @return the basket item to be saved
	 */
	public BasketItem createNewItem(BasketItem basketItem, Customer customer)
	{
		var basket = getBasket(customer);
		if (basketItem.getBasket() != null && !basket.equals(basketItem.getBasket()))
			throw new CorruptedDataException("corrupted basket id");
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
		basketRepository.save(basket);
		updateBasket(basket);
		return basketItem;
	}

	private int oldItemIndex(Basket basket, BasketItem oldItem)
	{
		var index = basket.getBasketItems().indexOf(oldItem);
		if (index < 0)
			throw new AbsentDataException("Basket item not found");
		return index;
	}

	/**
	 * Set a new {@code amount} for the basket item
	 * @param basketItem the basket item that exists in the basket
	 * @param customer the owner of the basket
	 * @return the modified basket item
	 */
	public BasketItem updateOldItem(BasketItem basketItem, Customer customer)
	{
		var basket = getBasket(customer);
		var index = oldItemIndex(basket, basketItem);
		var oldItem = basket.getBasketItems().get(index);
		oldItem.setAmount(basketItem.getAmount());
		var newItem = basketItemRepository.save(oldItem);
		updateBasket(basket);
		return newItem;
	}

	public void deleteItem(BasketItem basketItem, Customer customer)
	{
		var basket = getBasket(customer);
		var index = oldItemIndex(basket, basketItem);
		basketItemRepository.delete(basket.getBasketItems().get(index));
		updateBasket(basket);
	}

	public void clearBasket(Customer customer)
	{
		var basket = getBasket(customer);
		basketItemRepository.deleteAll(basket.getBasketItems());
	}
}
