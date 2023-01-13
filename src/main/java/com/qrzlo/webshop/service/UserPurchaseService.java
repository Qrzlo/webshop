package com.qrzlo.webshop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class UserPurchaseService
{
	private volatile Object lock = new Object();

	public UserPurchaseService()
	{
	}

	public Object getLock()
	{
		return lock;
	}
}
