package com.qrzlo.webshop.data.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Basket
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Column(name = "LAST_MODIFIED")
	private LocalDateTime lastModified = LocalDateTime.now();

	@OneToOne(optional = false)
	@JoinColumn(name = "CUSTOMER")
	private Customer customer;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "basket", cascade = CascadeType.PERSIST)
	@Size(min = 0)
	@OrderBy("addedAt ASC")
	private List<BasketItem> basketItems;

	public Double getTotalPrice()
	{
		return basketItems.stream().mapToDouble(item -> item.getAmount() * item.getInventory().getPrice()).sum();
	}
}
