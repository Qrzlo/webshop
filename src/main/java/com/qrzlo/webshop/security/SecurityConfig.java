package com.qrzlo.webshop.security;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Merchant;
import com.qrzlo.webshop.data.repository.CustomerRepository;
import com.qrzlo.webshop.data.repository.MerchantRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

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
					.mvcMatchers("/merchant", "/merchant/**").hasRole(SecurityConstant.MERCHANT_ROLE)
					.mvcMatchers("/api/address/**").hasRole(SecurityConstant.CUSTOMER_ROLE)
					.anyRequest().permitAll()
				.and()
					.formLogin()
						.loginPage("/login")
						.usernameParameter("email").passwordParameter("password")
						.successHandler((request, response, authentication) ->
						{
							List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
							if (authorities.contains(SecurityConstant.CUSTOMER_AUTHORITY))
							{
								response.sendRedirect("/customer");
							}
							else if (authorities.contains(SecurityConstant.MERCHANT_AUTHORITY))
							{
								response.sendRedirect("/merchant");
							}
							else if (authorities.contains(SecurityConstant.ADMIN_AUTHORITY))
							{
								response.sendRedirect("/admin");
							}
							else
							{
								response.sendRedirect("/");
							}
						})
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
