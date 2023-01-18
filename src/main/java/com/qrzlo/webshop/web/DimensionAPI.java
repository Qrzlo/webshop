package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Dimension;
import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.data.repository.DimensionRepository;
import com.qrzlo.webshop.data.repository.ProductRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/dimension",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class DimensionAPI
{
	private DimensionRepository dimensionRepository;
	private ProductRepository productRepository;

	public DimensionAPI(DimensionRepository dimensionRepository, ProductRepository productRepository)
	{
		this.dimensionRepository = dimensionRepository;
		this.productRepository = productRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Dimension dimension)
	{
		var newDimension = dimensionRepository.save(dimension);
		return ResponseEntity.ok(newDimension);
	}

	@GetMapping
	public ResponseEntity<?> read(@RequestParam(name = "product") Integer productId)
	{
		Product product = productRepository.findById(productId).orElseThrow();
		List<Dimension> dimensions = dimensionRepository.findDimensionsByProduct(product);
		return ResponseEntity.ok(dimensions);
	}
}


