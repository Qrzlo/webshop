package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Purchase;
import com.qrzlo.webshop.data.domain.PurchaseItem;
import com.qrzlo.webshop.data.repository.AddressRepository;
import com.qrzlo.webshop.data.repository.InventoryRepository;
import com.qrzlo.webshop.data.repository.PurchaseItemRepository;
import com.qrzlo.webshop.data.repository.PurchaseRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/purchase",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class PurchaseAPI
{
	private PurchaseRepository purchaseRepository;
	private AddressRepository addressRepository;
	private PurchaseItemRepository purchaseItemRepository;
	private InventoryRepository inventoryRepository;

	public PurchaseAPI(PurchaseRepository purchaseRepository, AddressRepository addressRepository, PurchaseItemRepository purchaseItemRepository, InventoryRepository inventoryRepository)
	{
		this.purchaseRepository = purchaseRepository;
		this.addressRepository = addressRepository;
		this.purchaseItemRepository = purchaseItemRepository;
		this.inventoryRepository = inventoryRepository;
	}

	@PostMapping("/initialize")
	public ResponseEntity<?> create(@RequestBody @Validated Purchase purchase,
									@AuthenticationPrincipal Customer customer)
	{
		try
		{
			if (!purchase.getCustomer().equals(customer))
				throw new Exception("customer id mismatch");
			var exist = purchaseRepository.findPurchaseByCustomerAndStatus(customer, Purchase.STATUS.INITIALIZED);
			if (exist.isPresent())
				throw new Exception("cannot initialize two active purchases");
			purchase.setStatus(Purchase.STATUS.INITIALIZED);
			purchase.setPurchaseItems(null);
			var newPurchase = purchaseRepository.save(purchase);
			return ResponseEntity.ok(newPurchase);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping(path = "/current")
	public ResponseEntity<?> read(@AuthenticationPrincipal Customer customer)
	{
		try
		{
			var initializedPurchase = purchaseRepository.findPurchaseByCustomerAndStatus(customer, Purchase.STATUS.INITIALIZED).orElseThrow();
			List<PurchaseItem> items = purchaseItemRepository.findPurchaseItemsByPurchase(initializedPurchase);
			if (!items.isEmpty())
			{
				initializedPurchase.setPurchaseItems(items);
				initializedPurchase.updatePrice();
				if (initializedPurchase.getAddress() != null)
					initializedPurchase.setPaidPrice(initializedPurchase.getTotalPrice() + 5.0);
			}

			return ResponseEntity.ok(initializedPurchase);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping(path = "/finalize")
	public ResponseEntity<?> finish(@AuthenticationPrincipal Customer customer)
	{
		try
		{
			var purchase = purchaseRepository.findPurchaseByCustomerAndStatus(customer, Purchase.STATUS.INITIALIZED).orElseThrow();
			var items = purchaseItemRepository.findPurchaseItemsByPurchase(purchase);
			if (items.isEmpty())
				throw new Exception("no items to be purchased");
			if (purchase.getAddress() == null)
				throw new Exception("address is not set");
			if (purchase.getTotalPrice() == null || purchase.getPaidPrice() == null)
				throw new Exception("prices are not calculated");
			purchase.setStatus(Purchase.STATUS.PLACED);
			purchaseRepository.save(purchase);
			items.forEach(i ->
			{
				var inventory = i.getInventory();
				inventory.setAmount(inventory.getAmount() - i.getAmount());
				inventoryRepository.save(inventory);
			});
			return ResponseEntity.ok(purchase);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping(path = "/address")	// to be changed, @RequestBody as Address
	public ResponseEntity<?> updateAddress(@RequestBody @Validated Purchase purchase,
									@AuthenticationPrincipal Customer customer)
	{
		try
		{
			if (!purchase.getCustomer().equals(customer))
				throw new Exception("customer id mismatch");
			var thePurchase = purchaseRepository.findById(purchase.getId()).orElseThrow();
			if (!customer.equals(thePurchase.getCustomer()))
				throw new Exception("you cannot modify a purchase of others");
			if (thePurchase.getStatus() != Purchase.STATUS.INITIALIZED)
				throw new Exception("the purchase cannot be modified");
			var theAddress = addressRepository.findById(purchase.getAddress().getId()).orElseThrow();
			if (!theAddress.getCustomer().equals(customer))
				throw new Exception("address is not from the customer");
			thePurchase.setAddress(theAddress);
			var newPurchase = purchaseRepository.save(thePurchase);
			return ResponseEntity.ok(newPurchase);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}


}
