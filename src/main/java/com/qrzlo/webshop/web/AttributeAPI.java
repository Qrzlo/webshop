package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Attribute;
import com.qrzlo.webshop.data.repository.AttributeRepository;
import com.qrzlo.webshop.data.repository.DimensionRepository;
import com.qrzlo.webshop.data.repository.VariantRepository;
import com.qrzlo.webshop.util.exception.InvalidRequestException;
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
		var variant = variantRepository.findById(attribute.getVariant().getId()).orElseThrow();
		var dimension = dimensionRepository.findById(attribute.getDimension().getId()).orElseThrow();
		var old = attributeRepository.findAttributeByDimensionAndVariant(dimension, variant);
		if (old != null)
			throw new InvalidRequestException("duplicate found!");
		var product = variant.getProduct();
		var exist = dimensionRepository.findDimensionsByProduct(product).contains(dimension);
		if (!exist)
			throw new InvalidRequestException("this pair of dimension/variant already exists!");
		var newAttribute = attributeRepository.save(attribute);
		return ResponseEntity.ok(newAttribute);
	}

	@GetMapping(path = "/variant")
	public ResponseEntity<?> readByVariant(@RequestParam(name = "variant") Long variantId)
	{
		var variant = variantRepository.findById(variantId).orElseThrow();
		Set<Attribute> attributes = attributeRepository.findAttributesByVariant(variant);
		return ResponseEntity.ok(attributes);
	}

	@GetMapping(path = "/dimension")
	public ResponseEntity<?> readByDimension(@RequestParam(name = "dimension") Integer dimensionId)
	{
		var dimension = dimensionRepository.findById(dimensionId).orElseThrow();
		Set<Attribute> attributes = attributeRepository.findAttributesByDimension(dimension);
		return ResponseEntity.ok(attributes);
	}

}
