package com.qrzlo.webshop.service;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.PurchaseItem;
import com.qrzlo.webshop.data.repository.InventoryRepository;
import com.qrzlo.webshop.data.repository.PurchaseItemRepository;
import com.qrzlo.webshop.data.repository.PurchaseRepository;
import com.qrzlo.webshop.util.exception.AbsentDataException;
import com.qrzlo.webshop.util.exception.CorruptedDataException;
import com.qrzlo.webshop.util.exception.InvalidRequestException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PurchaseItemService
{
	private PurchaseItemRepository purchaseItemRepository;
	private PurchaseRepository purchaseRepository;
	private InventoryRepository inventoryRepository;

	private PurchaseService purchaseService;

	public PurchaseItemService(PurchaseItemRepository purchaseItemRepository, PurchaseRepository purchaseRepository, InventoryRepository inventoryRepository, PurchaseService purchaseService)
	{
		this.purchaseItemRepository = purchaseItemRepository;
		this.purchaseRepository = purchaseRepository;
		this.inventoryRepository = inventoryRepository;
		this.purchaseService = purchaseService;
	}

	public Iterable<PurchaseItem> create(List<PurchaseItem> purchaseItems, Customer customer)
	{
		var purchase = purchaseService.readCurrent(customer);

		if (!purchaseItemRepository.findPurchaseItemsByPurchase(purchase).isEmpty())
			throw new InvalidRequestException("Please use PUT to modify existing purchase items");

		Set<Long> inventorySet = new HashSet<>(purchaseItems.size());
		purchaseItems.stream().forEach(i ->
		{
			var inventoryId = i.getInventory().getId();
			if (inventorySet.contains(inventoryId))
				throw new InvalidRequestException("inventory duplicates");
			inventorySet.add(inventoryId);
			var inventory = inventoryRepository.findById(inventoryId)
					.orElseThrow(() -> new AbsentDataException("The inventory cannot be found"));
			if (inventory.getAmount() < i.getAmount())
				throw new InvalidRequestException("The inventory does not have enough stocks");
			if (i.getPurchase() != null && !purchase.equals(i.getPurchase()))
				throw new CorruptedDataException("Purchase item and purchase mismatch");
			i.setPurchase(purchase);
			i.setInventory(inventory);
		});
		var results = purchaseItemRepository.saveAll(purchaseItems);
		purchaseService.readCurrent(customer);
		return results;
	}

	/**
	 * For now updating the purchase items, not basket items, is disallowed.
	 * This method should not be used until further improvement.
	 * @param purchaseItem
	 * @param customer
	 * @return
	 */
	@Deprecated
	private PurchaseItem update(PurchaseItem purchaseItem, Customer customer)
	{
		var oldItem = purchaseItemRepository.findById(purchaseItem.getId()).orElseThrow();
		var purchase = purchaseService.readCurrent(customer);
		if (oldItem.getInventory() != null && !oldItem.getInventory().equals(purchaseItem.getInventory()))
			throw new InvalidRequestException("cannot change the inventory here. please restart the whole process again");
		if (purchaseItem.getAmount() == 0)
			throw new InvalidRequestException("please use DELETE to delete the item");

		oldItem.setAmount(purchaseItem.getAmount());
		var newItem = purchaseItemRepository.save(oldItem);
		var totalPrice = purchase.getPurchaseItems()
				.stream()
				.mapToDouble(i -> i.getInventory().getPrice() * i.getAmount())
				.sum();
		purchase.setTotalPrice(totalPrice);
		purchaseRepository.save(purchase);
		return newItem;
	}

	/**
	 * This should only be invoked for an initialized purchase.
	 * For now deleting the purchase items is disallowed.
	 * This method should not be used until further improvement.
	 * @param purchaseItem
	 * @param customer
	 * @return
	 */
	@Deprecated
	private boolean delete(PurchaseItem purchaseItem, Customer customer)
	{
		var theOldItem = purchaseItemRepository.findById(purchaseItem.getId()).orElseThrow();
		var purchase = purchaseService.readCurrent(customer);
		purchaseItemRepository.delete(theOldItem);
		var totalPrice = purchase.getPurchaseItems()
				.stream()
				.mapToDouble(i -> i.getInventory().getPrice() * i.getAmount())
				.sum();
		purchase.setTotalPrice(totalPrice);
		purchaseRepository.save(purchase);
		return true;
	}

}
