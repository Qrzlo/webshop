package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
@Entity
public class Address
{
	@Id
	// to be changed, so that multiple address adding will not result in null pointer exception!!
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@TableGenerator(name = "address_generator", table = "id_generator",
//			pkColumnName = "name", valueColumnName = "value")
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

	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "CUSTOMER")
	private Customer customer;

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Address address = (Address) o;
		return Objects.equals(id, address.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Address{" +
				"id=" + id +
				", street='" + street + '\'' +
				", city='" + city + '\'' +
				", postalCode='" + postalCode + '\'' +
				", country='" + country + '\'' +
				'}';
	}
}
