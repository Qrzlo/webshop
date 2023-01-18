package com.qrzlo.webshop.util;

import com.qrzlo.webshop.util.exception.AbsentDataException;
import com.qrzlo.webshop.util.exception.CorruptedDataException;
import com.qrzlo.webshop.util.exception.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@ResponseBody
@RestControllerAdvice
public class RestAdvice
{

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public RestError validationException(MethodArgumentNotValidException e)
	{
		return new RestError("The parameters sent are not valid", e.getMessage());
	}

	@ExceptionHandler(CorruptedDataException.class)
	public RestError corruptedData(CorruptedDataException e)
	{
		return new RestError("The request data does not match database records", e.getMessage());
	}

	@ExceptionHandler({NoSuchElementException.class, AbsentDataException.class})
	public RestError relevantDataNotFound(NoSuchElementException e)
	{
		return new RestError("The relevant data to be modified cannot be found", e.getMessage());
	}

	@ExceptionHandler(InvalidRequestException.class)
	public RestError badRequest(InvalidRequestException e)
	{
		return new RestError("The request is invalid", e.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public RestError catchAll(RuntimeException e)
	{
		return new RestError("Unknown error", e.getMessage());
	}

}
