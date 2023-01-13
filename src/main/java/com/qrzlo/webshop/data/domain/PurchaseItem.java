package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.*;
import com.qrzlo.webshop.data.Views;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Entity
@JsonIdentityInfo(scope = PurchaseItem.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class PurchaseItem
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonView({Views.Order.class, Views.Checkout.class})
	@NotNull
	@Min(0)
	private Integer amount;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PURCHASE")
	private Purchase purchase;

	@JsonView({Views.Order.class, Views.Checkout.class})
	@ManyToOne(optional = false)
	@JoinColumn(name = "INVENTORY")
	private Inventory inventory;
}
