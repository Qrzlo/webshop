package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.data.domain.Variant;
import com.qrzlo.webshop.data.repository.ProductRepository;
import com.qrzlo.webshop.data.repository.VariantRepository;
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
	private VariantRepository variantRepository;
	private ProductRepository productRepository;

	public VariantAPI(VariantRepository variantRepository, ProductRepository productRepository)
	{
		this.variantRepository = variantRepository;
		this.productRepository = productRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Variant variant)
	{
		try
		{
			// existing: 0, 1, many; 0 singular or 1 singular
			Set<Variant> variants = variantRepository.findVariantsByProduct(variant.getProduct());
			if (variants.isEmpty())	// 0
			{
				variant.setSingular(true);
			}
			else if (variant.getSingular())	// >= 1, change the singular variant
			{
				variants.stream().filter(Variant::getSingular).forEach(v -> v.setSingular(false));
			}
			var newVariant = variantRepository.save(variant);
			return ResponseEntity.status(HttpStatus.CREATED).body(newVariant);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping
	public ResponseEntity<?> read(@RequestParam(name = "product") Integer productId)
	{
		try
		{
			Product product = productRepository.findById(productId).orElseThrow();
			Set<Variant> variants = variantRepository.findVariantsByProduct(product);
			return ResponseEntity.ok(variants);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}
}
