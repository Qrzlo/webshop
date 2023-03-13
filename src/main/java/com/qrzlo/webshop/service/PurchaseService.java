package com.qrzlo.webshop.service;

import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Purchase;
import com.qrzlo.webshop.data.domain.PurchaseItem;
import com.qrzlo.webshop.data.repository.*;
import com.qrzlo.webshop.util.exception.AbsentDataException;
import com.qrzlo.webshop.util.exception.CorruptedDataException;
import com.qrzlo.webshop.util.exception.InvalidRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Service
public class PurchaseService
{
	private PurchaseRepository purchaseRepository;
	private AddressRepository addressRepository;
	private PurchaseItemRepository purchaseItemRepository;

	private UserPurchaseService userPurchaseService;
	private InventoryLock inventoryLock;
	private TransactionService transactionService;

	public PurchaseService(PurchaseRepository purchaseRepository, AddressRepository addressRepository, PurchaseItemRepository purchaseItemRepository, InventoryRepository inventoryRepository, BasketRepository basketRepository, BasketItemRepository basketItemRepository, UserPurchaseService userPurchaseService, InventoryLock inventoryLock, TransactionService transactionService)
	{
		this.purchaseRepository = purchaseRepository;
		this.addressRepository = addressRepository;
		this.purchaseItemRepository = purchaseItemRepository;
		this.userPurchaseService = userPurchaseService;
		this.inventoryLock = inventoryLock;
		this.transactionService = transactionService;
	}

	/**
	 * Only one active purchase can be initialized by each customer
	 * @param customer
	 * @return
	 */
	public Purchase initialize(Customer customer)
	{
		synchronized (userPurchaseService.getLock())
		{
			var exist = purchaseRepository.findPurchaseByCustomerAndStatus(customer, Purchase.STATUS.INITIALIZED);
			exist.ifPresent(p -> cancelInitializedPurchase(customer));
			Purchase purchase = new Purchase();
			purchase.setStatus(Purchase.STATUS.INITIALIZED);
			purchase.setCustomer(customer);
			return purchaseRepository.save(purchase);
		}
	}

	/**
	 * Read the currently active purchase. Update the purchase price and price to be paid if the address is set
	 * @param customer
	 * @return
	 */
	public Purchase readCurrent(Customer customer)
	{
		var initializedPurchase = purchaseRepository
				.findPurchaseByCustomerAndStatus(customer, Purchase.STATUS.INITIALIZED)
				.orElseThrow(() -> new AbsentDataException("Cannot find the initialized purchase"));
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
			{
				String country = initializedPurchase.getAddress().getCountry();
				Double shippingCost = 10d;
				if ("NL".equalsIgnoreCase(country))
					shippingCost = 5d;
				initializedPurchase.setPaidPrice(initializedPurchase.getTotalPrice() + shippingCost);
			}
		}
		var updatedPurchase = purchaseRepository.save(initializedPurchase);
		return updatedPurchase;
	}

	/**
	 * Not used by the frontend
	 * @param year
	 * @param customer
	 * @return
	 */
	public List<Purchase> readByYear(Integer year, Customer customer)
	{
		final Integer THIS_YEAR = LocalDate.now().getYear();
		if (year == null)
			year = THIS_YEAR;
		if (year < 2020 || year > THIS_YEAR)
			throw new InvalidRequestException("year is not in range");
		var from = LocalDateTime.of(year, 1, 1, 0, 0);
		var to = LocalDateTime.of(year, 12, 31, 23, 59, 59);
		return purchaseRepository.findPurchasesByCustomerAndCreatedAtIsBetweenOrderByCreatedAt(customer, from, to);
	}

	/**
	 * Finalize a purchase: ensure there are some items, the address is not null, and the prices are calculated
	 * Acquire the locks for all the {@code inventory} objects order by their ids
	 * @param customer
	 * @return
	 */
	public Purchase finalizePurchase(Customer customer)
	{
		var purchase = readCurrent(customer);
		var items = purchaseItemRepository.findPurchaseItemsByPurchase(purchase);
		if (items.isEmpty())
			throw new InvalidRequestException("no items to be purchased");
		if (purchase.getAddress() == null)
			throw new InvalidRequestException("address is not set");
		if (purchase.getTotalPrice() == null || purchase.getPaidPrice() == null)
			throw new InvalidRequestException("prices are not calculated");
		var locks = new ArrayList<Lock>(items.size());
		Purchase placedPurchase;
		try
		{
			items.sort(Comparator.comparing(i -> i.getInventory().getId()));
			items.forEach(item ->
			{
				var lock = inventoryLock.getLock(item.getInventory());
				while (!lock.tryLock())
				{
				}
				locks.add(lock);
			});
			placedPurchase = transactionService.processPurchase(customer, purchase, items);
		}
		finally
		{
			locks.forEach(Lock::unlock);
		}
		return placedPurchase;
	}

	public void cancelInitializedPurchase(Customer customer)
	{
		var initializedPurchase = purchaseRepository
			.findPurchaseByCustomerAndStatus(customer, Purchase.STATUS.INITIALIZED)
			.orElseThrow(() -> new AbsentDataException("Cannot find the initialized purchase"));
		var purchaseItems = purchaseItemRepository.findPurchaseItemsByPurchase(initializedPurchase);
		purchaseItemRepository.deleteAll(purchaseItems);
		purchaseRepository.delete(initializedPurchase);
	}

	/**
	 * Associate an address of the customer with the current active purchase
	 * @param address
	 * @param customer
	 * @return
	 */
	public Purchase updatePurchaseAddress(Address address, Customer customer)
	{
		var initializedPurchase = readCurrent(customer);
		var theAddress = addressRepository
				.findByIdAndDeleted(address.getId(), false)
				.orElseThrow(() -> new AbsentDataException("Cannot find the address"));
		if (!theAddress.getCustomer().equals(customer))
			throw new CorruptedDataException("This address is not from the customer");
		initializedPurchase.setAddress(theAddress);
		return purchaseRepository.save(initializedPurchase);
	}

	public List<Purchase> readAll(Customer customer)
	{
		var all = purchaseRepository.findPurchasesByCustomerOrderByCreatedAt(customer);
		return all.stream().filter(p -> p.getStatus() != Purchase.STATUS.INITIALIZED).collect(Collectors.toList());
	}

}
