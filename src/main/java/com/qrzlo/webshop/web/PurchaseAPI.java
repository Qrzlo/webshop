package com.qrzlo.webshop.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.qrzlo.webshop.data.Views;
import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Purchase;
import com.qrzlo.webshop.data.domain.PurchaseItem;
import com.qrzlo.webshop.data.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
	private BasketRepository basketRepository;
	private BasketItemRepository basketItemRepository;

	public PurchaseAPI(PurchaseRepository purchaseRepository, AddressRepository addressRepository, PurchaseItemRepository purchaseItemRepository, InventoryRepository inventoryRepository)
	{
		this.purchaseRepository = purchaseRepository;
		this.addressRepository = addressRepository;
		this.purchaseItemRepository = purchaseItemRepository;
		this.inventoryRepository = inventoryRepository;
	}

	@GetMapping("/initialize")
	public ResponseEntity<?> create(@AuthenticationPrincipal Customer customer)
	{
		try
		{
			var exist = purchaseRepository.findPurchaseByCustomerAndStatus(customer, Purchase.STATUS.INITIALIZED);
			if (exist.isPresent())
				return read(customer);
			Purchase purchase = new Purchase();
			purchase.setStatus(Purchase.STATUS.INITIALIZED);
			purchase.setCustomer(customer);
			var newPurchase = purchaseRepository.save(purchase);
			return ResponseEntity.ok(newPurchase);
		}
		catch (Exception e)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
		}
	}

	@JsonView(Views.Purchase.class)
	@GetMapping("/all")
	public ResponseEntity<?> readAll(@AuthenticationPrincipal Customer customer)
	{
		try
		{
			var purchases = purchaseRepository.findPurchasesByCustomerOrderByCreatedAt(customer);
			return ResponseEntity.ok(purchases);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/year")
	public ResponseEntity<?> readByYear(@RequestParam(name = "year", required = false) Integer year,
										@AuthenticationPrincipal Customer customer)
	{
		try
		{
			final Integer THIS_YEAR = LocalDate.now().getYear();
			if (year == null)
				year = THIS_YEAR;
			if (year < 2020 || year > THIS_YEAR)
				throw new Exception("year is not in range");
			var from = LocalDateTime.of(year, 1, 1, 0, 0);
			var to = LocalDateTime.of(year, 12, 31, 23, 59, 59);
			var purchases = purchaseRepository.findPurchasesByCustomerAndCreatedAtIsBetweenOrderByCreatedAt(customer, from, to);
			return ResponseEntity.ok(purchases);
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
				var totalPrice = items
						.stream()
						.mapToDouble(i -> i.getInventory().getPrice() * i.getAmount())
						.sum();
				initializedPurchase.setTotalPrice(totalPrice);
				if (initializedPurchase.getAddress() != null)
					initializedPurchase.setPaidPrice(initializedPurchase.getTotalPrice() + 5);
			}
			var updatedPurchase = purchaseRepository.save(initializedPurchase);
			return ResponseEntity.ok(updatedPurchase);
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
			var basket = basketRepository.findBasketByCustomer(customer);
			basket.getBasketItems().forEach(basketItemRepository::delete);
			return ResponseEntity.ok(purchase);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping(path = "/address")	// to be changed, @RequestBody as Address
	public ResponseEntity<?> updateAddress(@RequestBody Address address,
										   @AuthenticationPrincipal Customer customer)
	{
		try
		{
			var initializedPurchase = purchaseRepository.findPurchaseByCustomerAndStatus(customer, Purchase.STATUS.INITIALIZED).orElseThrow();
			var theAddress = addressRepository.findById(address.getId()).orElseThrow();
			if (!theAddress.getCustomer().equals(customer))
				throw new Exception("address is not from the customer");
			initializedPurchase.setAddress(theAddress);
			initializedPurchase.setPaidPrice(initializedPurchase.getTotalPrice() + 5);
			var newPurchase = purchaseRepository.save(initializedPurchase);
			return ResponseEntity.ok(newPurchase);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}


}
