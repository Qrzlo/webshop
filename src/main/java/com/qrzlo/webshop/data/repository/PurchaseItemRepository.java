package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Purchase;
import com.qrzlo.webshop.data.domain.PurchaseItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurchaseItemRepository extends CrudRepository<PurchaseItem, Long>
{
	List<PurchaseItem> findPurchaseItemsByPurchase(Purchase purchase);
}
