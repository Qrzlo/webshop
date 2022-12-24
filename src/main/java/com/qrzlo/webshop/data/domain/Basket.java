package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.*;
import com.qrzlo.webshop.data.Views;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Basket
{
	@JsonView(Views.Basket.class)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonView(Views.Basket.class)
	@NotNull
	@Column(name = "LAST_MODIFIED")
	private LocalDateTime lastModified = LocalDateTime.now();

	@JsonIgnore
	@OneToOne(optional = false)
	@JoinColumn(name = "CUSTOMER")
	private Customer customer;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(Views.Basket.class)
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "basket")
	@OrderBy("addedAt ASC")
	private List<BasketItem> basketItems;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(Views.Basket.class)
	private transient Double totalPrice;

	public Double updatePrice()
	{
		this.totalPrice = this.getBasketItems()
				.stream()
				.mapToDouble(i -> i.getInventory().getPrice() * i.getAmount())
				.sum();
		return totalPrice;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Basket basket = (Basket) o;
		return Objects.equals(id, basket.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Basket{" +
				"id=" + id +
				", lastModified=" + lastModified +
				", totalPrice=" + totalPrice +
				'}';
	}
}
