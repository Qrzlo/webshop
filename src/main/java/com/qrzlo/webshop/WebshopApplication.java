package com.qrzlo.webshop;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.repository.CustomerRepository;
import com.qrzlo.webshop.web.CustomerAPI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebshopApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(WebshopApplication.class, args);
	}

	@Bean
	public CommandLineRunner customerAndBasket(CustomerAPI customerAPI, CustomerRepository customerRepository)
	{
		return args ->
		{
			var exist = customerRepository.findCustomerByEmail("x@c.com");
			if (exist != null)
				return;
			Customer customer = new Customer();
			customer.setEmail("x@c.com");
			customer.setName("namec");
			customer.setPassword("passwordx");
			customerAPI.create(customer);
		};
	}
}
