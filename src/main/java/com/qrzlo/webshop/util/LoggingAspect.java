package com.qrzlo.webshop.util;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect
{
	private static final Logger logger = LoggerFactory.getLogger("LoggingAspect");

	@AfterThrowing(pointcut = "execution(* com.qrzlo.webshop.service.*.*(..))", throwing = "throwable")
	public void logExceptions(Throwable throwable)
	{
		logger.error(throwable.getMessage());
	}
}
