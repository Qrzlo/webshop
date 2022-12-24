package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.*;
import com.qrzlo.webshop.data.Views;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
@Entity
@JsonIdentityInfo(scope = Address.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Address
{
	@Id
	// to be changed, so that multiple address adding will not result in null pointer exception!!
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@TableGenerator(name = "address_generator", table = "id_generator",
//			pkColumnName = "name", valueColumnName = "value")
	private Integer id;

	@JsonView(Views.Purchase.class)
	@NotNull
	@Size(min = 1, max = 60)
	private String street;
	@JsonView(Views.Purchase.class)
	@NotNull
	@Size(min = 1, max = 60)
	private String city;
	@JsonView(Views.Purchase.class)
	@NotNull
	@Size(min = 1, max = 30)
	private String postalCode;
	@JsonView(Views.Purchase.class)
	@NotNull
	@Size(min = 2, max = 2)
	private String country;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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
