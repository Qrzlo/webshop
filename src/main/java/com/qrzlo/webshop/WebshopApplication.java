package com.qrzlo.webshop;

import com.qrzlo.webshop.data.domain.Basket;
import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Merchant;
import com.qrzlo.webshop.data.repository.BasketRepository;
import com.qrzlo.webshop.data.repository.CustomerRepository;
import com.qrzlo.webshop.data.repository.MerchantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class WebshopApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(WebshopApplication.class, args);
	}

	@Bean
	public CommandLineRunner customerAndBasket(CustomerRepository customerRepository, BasketRepository basketRepository, MerchantRepository merchantRepository)
	{
		return args ->
		{
			if (customerRepository.count() < 1)
			{
				for (int i = 0; i < 10; i++)
				{
					String random = UUID.randomUUID().toString();
					String email = random.replace("-", "");
					Customer customer = new Customer();
					customer.setUsername("user" + (1+i));
					customer.setEmail(email);
					Basket basket = new Basket();
					basket.setCustomer(customer);
					customer.setBasket(basket);
					customerRepository.save(customer);
					basketRepository.save(basket);
				}

			}
			if (merchantRepository.count() < 1)
			{
				for (int i = 0; i < 10; i++)
				{
					String random = UUID.randomUUID().toString();
					String email = random.replace("-", "");
					Merchant merchant = new Merchant();
					merchant.setEmail(email);
					merchant.setBrand("brand" + (1+i));
					merchant.setDescription("some description");
					merchantRepository.save(merchant);
				}
			}

		};
	}
}
