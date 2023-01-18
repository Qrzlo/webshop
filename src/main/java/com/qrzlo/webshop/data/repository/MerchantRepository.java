package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Merchant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MerchantRepository extends CrudRepository<Merchant, Integer>
{
	Merchant findMerchantByEmail(String emailAsUsername);
	List<Merchant> findAll();
}
