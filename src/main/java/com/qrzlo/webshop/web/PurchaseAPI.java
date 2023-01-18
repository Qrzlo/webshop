package com.qrzlo.webshop.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.qrzlo.webshop.data.Views;
import com.qrzlo.webshop.data.domain.Address;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Purchase;
import com.qrzlo.webshop.service.PurchaseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/purchase",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class PurchaseAPI
{

	private PurchaseService purchaseService;

	public PurchaseAPI(PurchaseService purchaseService)
	{
		this.purchaseService = purchaseService;
	}


	@JsonView(Views.Checkout.class)
	@GetMapping("/initialize")
	public ResponseEntity<Purchase> create(@AuthenticationPrincipal Customer customer)
	{
		return ResponseEntity.ok(purchaseService.initialize(customer));
	}

	@JsonView(Views.Order.class)
	@GetMapping("/all")
	public ResponseEntity<List<Purchase>> readAll(@AuthenticationPrincipal Customer customer)
	{
		return ResponseEntity.ok(purchaseService.readAll(customer));
	}

	@GetMapping("/year")
	public ResponseEntity<?> readByYear(@RequestParam(name = "year", required = false) Integer year,
										@AuthenticationPrincipal Customer customer)
	{
		return ResponseEntity.ok(purchaseService.readByYear(year, customer));
	}

	@JsonView(Views.Checkout.class)
	@GetMapping(path = "/current")
	public ResponseEntity<Purchase> read(@AuthenticationPrincipal Customer customer)
	{
		return ResponseEntity.ok(purchaseService.readCurrent(customer));
	}

	@JsonView(Views.Checkout.class)
	@GetMapping(path = "/finalize")
	public ResponseEntity<Purchase> finish(@AuthenticationPrincipal Customer customer)
	{
		return ResponseEntity.ok(purchaseService.finalizePurchase(customer));
	}

	@JsonView(Views.Checkout.class)
	@PutMapping(path = "/address")
	public ResponseEntity<Purchase> updateAddress(@RequestBody Address address,
										   @AuthenticationPrincipal Customer customer)
	{
		return ResponseEntity.ok(purchaseService.updatePurchaseAddress(address, customer));
	}

	@JsonView(Views.Checkout.class)
	@GetMapping(path = "/cancel")
	public ResponseEntity cancelInitializedPurchase(@AuthenticationPrincipal Customer customer)
	{
		purchaseService.cancelInitializedPurchase(customer);
		return ResponseEntity.ok().build();
	}
}
