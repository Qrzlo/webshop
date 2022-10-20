package com.qrzlo.webshop.security;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Merchant;
import com.qrzlo.webshop.data.repository.CustomerRepository;
import com.qrzlo.webshop.data.repository.MerchantRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.qrzlo.webshop.security.SecurityConstant.MERCHANT_ROLE;

@Configuration
public class SecurityConfig
{
	@Bean
	public PasswordEncoder bcrypt()
	{
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(CustomerRepository customerRepository, MerchantRepository merchantRepository)
	{
		return email ->
		{
			Customer customer = customerRepository.findCustomerByEmail(email);
			Merchant merchant = merchantRepository.findMerchantByEmail(email);
			if (customer == null)
			{
				System.out.println("customer is null");
				if (merchant == null)
				{
					throw new UsernameNotFoundException("email: " + email + " is not found");
				}
				else
				{
					return merchant;
				}
			}
			else
			{
				if (merchant == null)
				{
					return customer;
				}
				else
				{
					throw new UsernameNotFoundException("email is found to be registered to both a customer and a merchant");
				}
			}
		};
	}


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception
	{
		return httpSecurity
				.authorizeRequests()
					.mvcMatchers("/customer", "/customer/**").hasRole(SecurityConstant.CUSTOMER_ROLE)
					.mvcMatchers("/merchant", "/merchant/**").hasRole(MERCHANT_ROLE)
					.anyRequest().permitAll()
				.and()
					.formLogin()
						.loginPage("/login")
						.usernameParameter("email").passwordParameter("password")
						.successHandler((request, response, authentication) ->
						{
							var authorities = authentication.getAuthorities();
							if (authorities.contains(SecurityConstant.CUSTOMER_AUTHORITY))
							{
								response.sendRedirect("/customer");
							}
							else if (authorities.contains(SecurityConstant.MERCHANT_AUTHORITY))
							{
								response.sendRedirect("/merchant");
							}
						})
//						.successForwardUrl("/customer")
//						.defaultSuccessUrl("/customer", true)
						.failureHandler((request, response, exception) ->
						{
							System.out.println("in failure handler!! this is not working..");
							response.sendRedirect("/login");
						})
				.and().logout().logoutSuccessUrl("/login")
				.and().csrf().disable()
				.build();
	}
}
