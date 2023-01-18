package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Inventory;
import com.qrzlo.webshop.data.domain.Merchant;
import com.qrzlo.webshop.data.repository.InventoryRepository;
import com.qrzlo.webshop.data.repository.MerchantRepository;
import com.qrzlo.webshop.data.repository.VariantRepository;
import com.qrzlo.webshop.util.exception.CorruptedDataException;
import com.qrzlo.webshop.util.exception.InvalidRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(path = "/api/inventory",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class InventoryAPI
{
	private InventoryRepository inventoryRepository;
	private VariantRepository variantRepository;
	private MerchantRepository merchantRepository;
	private List<Merchant> allMerchants;

	public InventoryAPI(InventoryRepository inventoryRepository, VariantRepository variantRepository, MerchantRepository merchantRepository)
	{
		this.inventoryRepository = inventoryRepository;
		this.variantRepository = variantRepository;
		this.merchantRepository = merchantRepository;
		allMerchants = this.merchantRepository.findAll();
	}

	@GetMapping("/refresh")
	public ResponseEntity<?> refreshMerchants()
	{
		allMerchants = this.merchantRepository.findAll();
		return ResponseEntity.ok(allMerchants);
	}

	@GetMapping("/random")
	public ResponseEntity<?> random(@RequestParam("variant") Long variantId,
									@RequestParam("price") Double price)
	{
		var variant = variantRepository.findById(variantId).orElseThrow();
		Random random = new Random();
		int merchantAmount = random.nextInt(4);
		var merchants = new ArrayList<>(allMerchants);
		List<Inventory> inventories = new ArrayList<>(merchantAmount);
		for (int i = 0; i < merchantAmount; i++)
		{
			var oneMerchant = merchants.get(random.nextInt(merchants.size()));
			merchants.remove(oneMerchant);
			Inventory inventory = new Inventory();
			inventory.setMerchant(oneMerchant);
			inventory.setVariant(variant);
			inventory.setPrice(price + random.nextInt(5));
			inventory.setAmount(random.nextInt(1, 100));
			inventories.add(inventory);
		}
		inventoryRepository.saveAll(inventories);
		return ResponseEntity.ok().build();
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Inventory inventory,
									@AuthenticationPrincipal Merchant merchant)
	{
		if (merchant.getId() != inventory.getMerchant().getId())
			throw new CorruptedDataException("not the same merchant!");
		var exist = inventoryRepository.findInventoryByVariantAndMerchant(inventory.getVariant(), merchant);
		if (exist != null)
			throw new InvalidRequestException("duplicate inventory entry found");
		var newInventory = inventoryRepository.save(inventory);
		return ResponseEntity.ok(newInventory);
	}

	@GetMapping
	public ResponseEntity<?> read(@AuthenticationPrincipal Merchant merchant)
	{
		return ResponseEntity.ok(inventoryRepository.findInventoriesByMerchant(merchant));
	}

	@GetMapping(path = "/variant")
	public ResponseEntity<?> read(@RequestParam(name = "variant") Long variantId)
	{
		var variant = variantRepository.findById(variantId).orElseThrow();
		List<Inventory> inventories =  inventoryRepository.findInventoriesByVariant(variant);
		return ResponseEntity.ok(inventories);
	}
}
