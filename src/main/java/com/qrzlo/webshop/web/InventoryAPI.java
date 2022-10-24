package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Inventory;
import com.qrzlo.webshop.data.domain.Merchant;
import com.qrzlo.webshop.data.repository.InventoryRepository;
import com.qrzlo.webshop.data.repository.VariantRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/inventory",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class InventoryAPI
{
	private InventoryRepository inventoryRepository;
	private VariantRepository variantRepository;

	public InventoryAPI(InventoryRepository inventoryRepository, VariantRepository variantRepository)
	{
		this.inventoryRepository = inventoryRepository;
		this.variantRepository = variantRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Inventory inventory,
									@AuthenticationPrincipal Merchant merchant)
	{
		try
		{
			if (merchant.getId() != inventory.getMerchant().getId())
				throw new Exception("not the same merchant!");
			var newInventory = inventoryRepository.save(inventory);
			return ResponseEntity.ok(newInventory);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping
	public ResponseEntity<?> read(@AuthenticationPrincipal Merchant merchant)
	{
		return ResponseEntity.ok(inventoryRepository.findInventoriesByMerchant(merchant));
	}

	@GetMapping(path = "/variant")
	public ResponseEntity<?> read(@RequestParam(name = "variant") Long variantId)
	{
		try
		{
			var variant = variantRepository.findById(variantId).orElseThrow();
			List<Inventory> inventories =  inventoryRepository.findInventoriesByVariant(variant);
			return ResponseEntity.ok(inventories);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}
}
