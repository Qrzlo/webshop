package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.Inventory;
import com.qrzlo.webshop.data.domain.Merchant;
import com.qrzlo.webshop.data.domain.Variant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InventoryRepository extends CrudRepository<Inventory, Long>
{
	List<Inventory> findInventoriesByVariant(Variant variant);
	List<Inventory> findInventoriesByMerchant(Merchant merchant);
}
