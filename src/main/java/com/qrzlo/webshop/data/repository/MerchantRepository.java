package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Merchant;
import org.springframework.data.repository.CrudRepository;

public interface MerchantRepository extends CrudRepository<Merchant, Integer>
{
}
