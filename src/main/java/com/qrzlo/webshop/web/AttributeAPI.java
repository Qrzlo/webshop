package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Attribute;
import com.qrzlo.webshop.data.repository.AttributeRepository;
import com.qrzlo.webshop.data.repository.DimensionRepository;
import com.qrzlo.webshop.data.repository.VariantRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(path = "/api/attribute",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class AttributeAPI
{
	private AttributeRepository attributeRepository;
	private DimensionRepository dimensionRepository;
	private VariantRepository variantRepository;

	public AttributeAPI(AttributeRepository attributeRepository, DimensionRepository dimensionRepository, VariantRepository variantRepository)
	{
		this.attributeRepository = attributeRepository;
		this.dimensionRepository = dimensionRepository;
		this.variantRepository = variantRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Attribute attribute)
	{
		try
		{
			var variant = variantRepository.findById(attribute.getVariant().getId()).orElseThrow();
			var dimension = dimensionRepository.findById(attribute.getDimension().getId()).orElseThrow();
			var old = attributeRepository.findAttributeByDimensionAndVariant(dimension, variant);
			if (old != null)
				throw new Exception("duplicate found!");
			var product = variant.getProduct();
			var exist = dimensionRepository.findDimensionsByProduct(product).contains(dimension);
			if (!exist)
				throw new Exception("this pair of dimension/variant already exists!");
			var newAttribute = attributeRepository.save(attribute);
			return ResponseEntity.ok(newAttribute);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping(path = "/variant")
	public ResponseEntity<?> readByVariant(@RequestParam(name = "variant") Long variantId)
	{
		try
		{
			var variant = variantRepository.findById(variantId).orElseThrow();
			Set<Attribute> attributes = attributeRepository.findAttributesByVariant(variant);
			return ResponseEntity.ok(attributes);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping(path = "/dimension")
	public ResponseEntity<?> readByDimension(@RequestParam(name = "dimension") Integer dimensionId)
	{
		try
		{
			var dimension = dimensionRepository.findById(dimensionId).orElseThrow();
			Set<Attribute> attributes = attributeRepository.findAttributesByDimension(dimension);
			return ResponseEntity.ok(attributes);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

}
