package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Customer
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Size(min = 1, max = 60)
	private String username;
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
	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "customer")
	private Set<Address> addresses;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
	@OrderBy("createdAt ASC")
	private List<Purchase> purchases;
}
