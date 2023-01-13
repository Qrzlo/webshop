package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.*;
import com.qrzlo.webshop.data.Views;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
//@JsonIdentityInfo(scope= Inventory.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Inventory
{
	@JsonView({Views.Basket.class, Views.Product.class, Views.Checkout.class})
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonView({Views.Basket.class, Views.Product.class})
	@NotNull
	@Min(0)
	private Integer amount;
	@JsonView({Views.Basket.class, Views.Product.class, Views.Order.class, Views.Checkout.class})
	@NotNull
	@Min(0)
	@Column(columnDefinition = "double(10, 2)")
	private Double price;
	@NotNull
	@Column(name = "LAST_MODIFIED")
	private LocalDateTime lastModified = LocalDateTime.now();

	@JsonView({Views.Product.class, Views.Order.class})
	@ManyToOne(optional = false)
	@JoinColumn(name = "MERCHANT")
	private Merchant merchant;
	@JsonView({Views.Basket.class, Views.Order.class, Views.Checkout.class})
	@ManyToOne(optional = false)
	@JoinColumn(name = "VARIANT")
	private Variant variant;

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Inventory inventory = (Inventory) o;
		return Objects.equals(id, inventory.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Inventory{" +
				"id=" + id +
				", amount=" + amount +
				", price=" + price +
				", lastModified=" + lastModified +
				'}';
	}
}
