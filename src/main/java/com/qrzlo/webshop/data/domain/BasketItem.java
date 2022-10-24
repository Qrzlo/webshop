package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
public class BasketItem
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Min(value = 1, message = "An amount of 0 means deleting this item from the basket")
	private Integer amount;
	@NotNull
	@Column(name = "ADDED_AT")
	private LocalDateTime addedAt = LocalDateTime.now();

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne(optional = false)
	@JoinColumn(name = "BASKET")
	private Basket basket;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne(optional = false)
	@JoinColumn(name = "INVENTORY")
	private Inventory inventory;

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BasketItem that = (BasketItem) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "BasketItem{" +
				"id=" + id +
				", amount=" + amount +
				", addedAt=" + addedAt +
				'}';
	}
}
