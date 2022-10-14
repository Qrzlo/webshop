package com.qrzlo.webshop.data.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
public class Address
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Size(min = 1, max = 60)
	private String street;
	@NotNull
	@Size(min = 1, max = 60)
	private String city;
	@NotNull
	@Size(min = 1, max = 30)
	private String postalCode;
	@NotNull
	@Size(min = 2, max = 2)
	private String country;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "CUSTOMER")
	private Customer customer;

}
