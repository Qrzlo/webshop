package com.qrzlo.webshop.data.domain;

import com.qrzlo.webshop.security.SecurityConstant;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
public class Merchant implements UserDetails
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Size(min = 1, max = 200)
	private String email;
	@NotNull
	@Size(min = 1, max = 100)
	private String brand;
	@NotNull
	@Size(max = 10000)
	private String description;
	@NotNull
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();

	@NotNull
	@Size(max = 1000)
	private String password;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return List.of(new SimpleGrantedAuthority(SecurityConstant.MERCHANT_AUTHORITY));
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
