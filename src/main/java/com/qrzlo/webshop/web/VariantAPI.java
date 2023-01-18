package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.data.domain.Variant;
import com.qrzlo.webshop.data.repository.ProductRepository;
import com.qrzlo.webshop.data.repository.VariantRepository;
import com.qrzlo.webshop.service.VariantService;
import com.qrzlo.webshop.util.exception.AbsentDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(path = "/api/variant", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
public class VariantAPI
{
	private VariantService variantService;

	public VariantAPI(VariantService variantService)
	{
		this.variantService = variantService;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Variant variant)
	{
		var newVariant = variantService.create(variant);
		return ResponseEntity.status(HttpStatus.CREATED).body(newVariant);
	}

	@GetMapping
	public ResponseEntity<?> read(@RequestParam(name = "product") Integer productId)
	{
		var variants = variantService.read(productId);
		return ResponseEntity.ok(variants);
	}
}
