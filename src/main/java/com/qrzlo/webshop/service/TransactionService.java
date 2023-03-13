package com.qrzlo.webshop.service;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Purchase;
import com.qrzlo.webshop.data.domain.PurchaseItem;
import com.qrzlo.webshop.data.repository.*;
import com.qrzlo.webshop.util.exception.AbsentDataException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class TransactionService
{
	private PurchaseRepository purchaseRepository;
	private InventoryRepository inventoryRepository;
	private BasketRepository basketRepository;
	private BasketItemRepository basketItemRepository;


	/**
	 * Start a transaction: decrement the {@code inventory} {@code amount}s accordingly
	 * Change the status of the purchase to PLACED
	 * Clear all the basket items of the customer
	 * @param customer
	 * @param purchase
	 * @param items the purchase items associated with this purchase
	 * @return
	 */
	public Purchase processPurchase(Customer customer, Purchase purchase, List<PurchaseItem> items)
	{
		items.forEach(i ->
		{
			var inventory = i.getInventory();
			inventory.setAmount(inventory.getAmount() - i.getAmount());
			inventoryRepository.save(inventory);
		});
		purchase.setStatus(Purchase.STATUS.PLACED);
		var placedPurchase = purchaseRepository.save(purchase);
		var basket = basketRepository
				.findBasketByCustomer(customer)
				.orElseThrow(() -> new AbsentDataException("Cannot find the basket of the customer"));
		basketItemRepository.deleteAll(basket.getBasketItems());
		return placedPurchase;
	}

}
