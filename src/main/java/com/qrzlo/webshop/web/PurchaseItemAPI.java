package com.qrzlo.webshop.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.qrzlo.webshop.data.Views;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Purchase;
import com.qrzlo.webshop.data.domain.PurchaseItem;
import com.qrzlo.webshop.data.repository.InventoryRepository;
import com.qrzlo.webshop.data.repository.PurchaseItemRepository;
import com.qrzlo.webshop.data.repository.PurchaseRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/purchaseitem",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class PurchaseItemAPI
{
	private PurchaseItemRepository purchaseItemRepository;
	private PurchaseRepository purchaseRepository;
	private InventoryRepository inventoryRepository;

	public PurchaseItemAPI(PurchaseItemRepository purchaseItemRepository, PurchaseRepository purchaseRepository, InventoryRepository inventoryRepository)
	{
		this.purchaseItemRepository = purchaseItemRepository;
		this.purchaseRepository = purchaseRepository;
		this.inventoryRepository = inventoryRepository;
	}

	@JsonView(Views.Checkout.class)
	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated List<PurchaseItem> purchaseItems,
									@AuthenticationPrincipal Customer customer)
	{
		try
		{
			// do not send a purchase: {id: x} field
			// multiple purchaseItems with this same field will result in "already had pojo for id..." error
			var purchase = getInitializedPurchase(customer, purchaseItems);

			if (!purchaseItemRepository.findPurchaseItemsByPurchase(purchase).isEmpty())
				throw new Exception("please use PUT to modify existing purchase items");

			Set<Long> inventorySet = new HashSet<>(purchaseItems.size());
			boolean allValidItems = purchaseItems.stream()
				.filter(i ->
				{	// find all invalid items here!
					// 1. validate Purchase field sent from the items
					// 2. validate the Inventory Field: no duplicates
					try
					{
						var inventoryId = i.getInventory().getId();
						if (inventorySet.contains(inventoryId))
							throw new Exception("inventory duplicates");
						inventorySet.add(inventoryId);
						var inventory = inventoryRepository.findById(inventoryId).orElseThrow();
						if (inventory.getAmount() < i.getAmount())
							throw new Exception("inventory is not sufficient for this item");
						if (i.getPurchase() != null && !purchase.equals(i.getPurchase()))
							throw new Exception("purchase item and purchase mismatch");
						i.setPurchase(purchase);
						i.setInventory(inventory);
						return false;
					}
					catch (Exception e)
					{
						return true;
					}
				})
				.findAny().isEmpty();
			if (!allValidItems)
				throw new Exception("some purchase items are invalid");
			purchaseItemRepository.saveAll(purchaseItems);
			var newPurchaseItems = purchaseItemRepository.findPurchaseItemsByPurchase(purchase);
			var totalPrice = purchaseItems.stream()
					.mapToDouble(i -> i.getInventory().getPrice() * i.getAmount())
					.sum();
			purchase.setTotalPrice(totalPrice);
			purchaseRepository.save(purchase);
			return ResponseEntity.ok(newPurchaseItems);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	private Purchase getInitializedPurchase(Customer customer, List<PurchaseItem> purchaseItems) throws Exception
	{
		var purchase = purchaseRepository.findPurchaseByCustomerAndStatus(customer, Purchase.STATUS.INITIALIZED).orElseThrow();
		for (PurchaseItem e : purchaseItems)
			validatePurchaseAndItem(purchase, e);
		return purchase;
	}

	private void validatePurchaseAndItem(Purchase purchase, PurchaseItem purchaseItem) throws Exception
	{
		if (purchaseItem.getPurchase() != null && !purchaseItem.getPurchase().equals(purchase))
			throw new Exception("purchase and item mismatch");
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestBody @Validated PurchaseItem purchaseItem,
									@AuthenticationPrincipal Customer customer)
	{
		try
		{
			var oldItem = purchaseItemRepository.findById(purchaseItem.getId()).orElseThrow();
			var purchase = getInitializedPurchase(customer, List.of(oldItem, purchaseItem));
			if (oldItem.getInventory() != null && !oldItem.getInventory().equals(purchaseItem.getInventory()))
				throw new Exception("cannot change the inventory here. please restart the whole process again");
			if (purchaseItem.getAmount() == 0)
				throw new Exception("please use DELETE to delete the item");

			oldItem.setAmount(purchaseItem.getAmount());
			var newItem = purchaseItemRepository.save(oldItem);
			var totalPrice = purchase.getPurchaseItems()
					.stream()
					.mapToDouble(i -> i.getInventory().getPrice() * i.getAmount())
					.sum();
			purchase.setTotalPrice(totalPrice);
			purchaseRepository.save(purchase);
			return ResponseEntity.ok(newItem);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping
	public ResponseEntity<?> delete(@RequestBody @Validated PurchaseItem purchaseItem,
									@AuthenticationPrincipal Customer customer)
	{
		try
		{
			var theOldItem = purchaseItemRepository.findById(purchaseItem.getId()).orElseThrow();
			var purchase = getInitializedPurchase(customer, List.of(theOldItem, purchaseItem));
			purchaseItemRepository.delete(theOldItem);
			var totalPrice = purchase.getPurchaseItems()
					.stream()
					.mapToDouble(i -> i.getInventory().getPrice() * i.getAmount())
					.sum();
			purchase.setTotalPrice(totalPrice);
			purchaseRepository.save(purchase);
			return ResponseEntity.ok(theOldItem);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}
}
