package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.BasketItem;
import org.springframework.data.repository.CrudRepository;

public interface BasketItemRepository extends CrudRepository<BasketItem, Long>
{
}
