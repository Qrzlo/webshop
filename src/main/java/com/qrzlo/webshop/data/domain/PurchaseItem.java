package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class PurchaseItem
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Min(0)
	private Integer amount;

	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "PURCHASE")
	private Purchase purchase;

	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "INVENTORY")
	private Inventory inventory;
}