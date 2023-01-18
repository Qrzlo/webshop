package com.qrzlo.webshop.service;

import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.data.domain.Variant;
import com.qrzlo.webshop.data.repository.ProductRepository;
import com.qrzlo.webshop.data.repository.VariantRepository;
import com.qrzlo.webshop.util.exception.AbsentDataException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class VariantService
{
	private VariantRepository variantRepository;
	private ProductRepository productRepository;

	public VariantService(VariantRepository variantRepository, ProductRepository productRepository)
	{
		this.variantRepository = variantRepository;
		this.productRepository = productRepository;
	}

	public Variant create(Variant variant)
	{
		Set<Variant> variants = variantRepository.findVariantsByProduct(variant.getProduct());
		if (variants.isEmpty())	// 0
		{
			variant.setSingular(true);
		}
		else if (variant.getSingular())	// >= 1:
		{
			variants.stream().filter(Variant::getSingular).forEach(v -> v.setSingular(false));
			variantRepository.saveAll(variants);
		}
		return variantRepository.save(variant);
	}

	public Set<Variant> read(Integer productId)
	{
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new AbsentDataException("The product cannot be found"));
		return variantRepository.findVariantsByProduct(product);
	}
}
