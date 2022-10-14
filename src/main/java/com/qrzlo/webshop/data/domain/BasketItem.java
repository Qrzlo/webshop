package com.qrzlo.webshop.data.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "BASKET")
	private Basket basket;

	@ManyToOne(optional = false)
	@JoinColumn(name = "VARIANT")
	private Variant variant;

	@ManyToOne(optional = false)
	@JoinColumn(name = "INVENTORY")
	private Inventory inventory;

}
