package com.qrzlo.webshop;

import com.qrzlo.webshop.data.domain.Product;
import com.qrzlo.webshop.data.domain.Variant;
import com.qrzlo.webshop.data.repository.ProductRepository;
import com.qrzlo.webshop.data.repository.VariantRepository;
import com.qrzlo.webshop.service.VariantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class VariantTests
{
	@Mock
	private VariantRepository variantRepository;

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private VariantService variantService;

	@Mock
	private Variant variant;
	@Mock
	private Variant variant2;

	@Mock
	private Product product;

	@Test
	public void createTest()
	{
		given(variant.getSingular()).willReturn(true);
		given(variant2.getSingular()).willReturn(true);
		given(variant.getProduct()).willReturn(product);
		given(variantRepository.findVariantsByProduct(product)).willReturn(Set.of(variant2));

		variantService.create(variant);

		verify(variant2).setSingular(false);
	}
}
