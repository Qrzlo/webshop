package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qrzlo.webshop.security.SecurityConstant;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
public class Customer implements UserDetails
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Size(min = 1, max = 60)
	private String name;
	@NotNull
	@Size(min = 6, max = 200)
	private String email;
	@NotNull
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();

	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "customer", optional = false)
	private Basket basket;
	@JsonIgnore
	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
	private Set<Address> addresses;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
	@OrderBy("createdAt ASC")
	private List<Purchase> purchases;

	@JsonIgnore
	@NotNull
	@Size(max = 1000)
	private String password;

	public boolean addAddress(Address newAddress)
	{
		return this.addresses.add(newAddress);
	}

	public boolean updateAddress(Address newAddress)
	{
		if (this.addresses.contains(newAddress))
		{
			this.addresses.removeIf(a -> a.equals(newAddress));
			this.addresses.add(newAddress);
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean deleteAddress(Address deleteAddress)
	{
		if (this.addresses.contains(deleteAddress))
		{
			System.out.println("containing this...");
			return this.addresses.remove(deleteAddress);
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Customer customer = (Customer) o;
		if (this.id == null) return false;
		return id.equals(customer.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Customer{" +
				"id=" + id +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", createdAt=" + createdAt +
				'}';
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return List.of(new SimpleGrantedAuthority(SecurityConstant.CUSTOMER_AUTHORITY));
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public String getUsername()
	{
		return email;
	}

	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}

	@Override
	public boolean isAccountNonLocked()
	{
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
}
