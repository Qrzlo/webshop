package com.qrzlo.webshop.util;

import lombok.Data;

@Data
public class RestError
{
	private String reason;
	private String detail;

	public RestError(String reason, String detail)
	{
		this.reason = reason;
		this.detail = detail;
	}
}
