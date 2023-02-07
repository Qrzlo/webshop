package com.qrzlo.webshop.service;

import com.qrzlo.webshop.data.domain.Inventory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class InventoryLock
{
	private ConcurrentHashMap<Long, Lock> locks;

	public InventoryLock()
	{
		locks = new ConcurrentHashMap<>();
	}

	/**
	 * Obtain the lock for the given inventory
	 * @param inventory
	 * @return
	 */
	public Lock getLock(Inventory inventory)
	{
		Long id = inventory.getId();
		locks.putIfAbsent(id, new ReentrantLock());
		return locks.get(id);
	}

}
