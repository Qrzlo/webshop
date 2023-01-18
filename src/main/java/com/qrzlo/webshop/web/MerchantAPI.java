package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Merchant;
import com.qrzlo.webshop.data.repository.MerchantRepository;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;

@RestController
@RequestMapping(path = "/api/merchant",
		produces = {MediaType.APPLICATION_JSON_VALUE},
		consumes = {MediaType.APPLICATION_JSON_VALUE})
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
		merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));
		Merchant created = merchantRepository.save(merchant);
		return created;
	}
}
