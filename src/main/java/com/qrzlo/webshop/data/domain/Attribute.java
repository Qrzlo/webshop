package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.qrzlo.webshop.data.Views;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
@Entity
@JsonIdentityInfo(scope = Attribute.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Attribute
{
	@JsonView({Views.Basket.class, Views.Product.class})
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonView({Views.Basket.class, Views.Product.class, Views.Purchase.class})
	@NotNull
	@Size(min = 1, max = 50)
	private String value;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne(optional = false)
	@JoinColumn(name = "variant")
	private Variant variant;
	@JsonView({Views.Basket.class, Views.Product.class, Views.Purchase.class})
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne(optional = false)
	@JoinColumn(name = "DIMENSION")
	private Dimension dimension;

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Attribute attribute = (Attribute) o;
		return Objects.equals(id, attribute.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Attribute{" +
				"id=" + id +
				", value='" + value + '\'' +
				'}';
	}
}
