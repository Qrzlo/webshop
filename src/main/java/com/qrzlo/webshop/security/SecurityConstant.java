package com.qrzlo.webshop.security;

public class SecurityConstant
{
	public static final String PREFIX = "ROLE_";
	public static final String CUSTOMER_ROLE = "CUSTOMER";
	public static final String MERCHANT_ROLE = "MERCHANT";
	public static final String ADMIN_ROLE = "ADMIN";

	public static final String CUSTOMER_AUTHORITY = PREFIX + CUSTOMER_ROLE;
	public static final String MERCHANT_AUTHORITY = PREFIX + MERCHANT_ROLE;
	public static final String ADMIN_AUTHORITY = PREFIX + ADMIN_ROLE;

}
