package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Merchant;
import com.qrzlo.webshop.data.repository.MerchantRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;

@RestController
@RequestMapping(path = "/merchant", produces = "application/json", consumes = "application/json")
public class MerchantAPI
{
	private PasswordEncoder passwordEncoder;
	private MerchantRepository merchantRepository;

	public MerchantAPI(PasswordEncoder passwordEncoder, MerchantRepository merchantRepository)
	{
		this.passwordEncoder = passwordEncoder;
		this.merchantRepository = merchantRepository;
	}

	@PostMapping
	public Merchant save(@RequestBody @Validated Merchant merchant)
	{
		try
		{
			merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));
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
