package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

	@JsonIgnore
	@OneToOne(optional = false)
	@JoinColumn(name = "CUSTOMER")
	private Customer customer;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "basket")
	@OrderBy("addedAt ASC")
	private List<BasketItem> basketItems;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private transient Double totalPrice;

	public Double updatePrice()
	{
		return this.getBasketItems().stream().mapToDouble(i -> i.getInventory().getPrice()).sum();
	}
}
