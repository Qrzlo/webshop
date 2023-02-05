package com.qrzlo.webshop.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.qrzlo.webshop.data.Views;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Purchase;
import com.qrzlo.webshop.data.domain.PurchaseItem;
import com.qrzlo.webshop.data.repository.InventoryRepository;
import com.qrzlo.webshop.data.repository.PurchaseItemRepository;
import com.qrzlo.webshop.data.repository.PurchaseRepository;
import com.qrzlo.webshop.service.PurchaseItemService;
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
	private PurchaseItemService purchaseItemService;

	public PurchaseItemAPI(PurchaseItemService purchaseItemService)
	{
		this.purchaseItemService = purchaseItemService;
	}

	@JsonView(Views.Checkout.class)
	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated List<PurchaseItem> purchaseItems,
									@AuthenticationPrincipal Customer customer)
	{
		var results = purchaseItemService.create(purchaseItems, customer);
		return ResponseEntity.ok(results);
	}
}
