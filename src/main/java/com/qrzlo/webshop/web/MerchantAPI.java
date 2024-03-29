package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Merchant;
import com.qrzlo.webshop.data.repository.MerchantRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;

@RestController
@RequestMapping(path = "/api/merchant", produces = "application/json", consumes = "application/json")
public class MerchantAPI
{
	private MerchantRepository merchantRepository;

	public MerchantAPI(MerchantRepository merchantRepository)
	{
		this.merchantRepository = merchantRepository;
	}

	@PostMapping
	public Merchant save(@RequestBody @Validated Merchant merchant)
	{
		try
		{
			Merchant created = merchantRepository.save(merchant);
			return created;
		}
		catch (ValidationException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
