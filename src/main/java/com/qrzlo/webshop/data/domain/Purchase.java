package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.qrzlo.webshop.data.Views;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@JsonIdentityInfo(scope = Purchase.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Purchase
{
	@JsonView({Views.Order.class, Views.Checkout.class})
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonView(Views.Order.class)
	@NotNull
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();
	@JsonView(Views.Order.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@NotNull
	@Enumerated(EnumType.STRING)
	private STATUS status = STATUS.PLACED;
	@JsonView({Views.Order.class, Views.Checkout.class})
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Min(0)
	@Column(name = "TOTAL_PRICE", columnDefinition = "double(10, 2)")
	private Double totalPrice;
	@JsonView({Views.Order.class, Views.Checkout.class})
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Min(0)
	@Column(name = "PAID_PRICE", columnDefinition = "double(10, 2)")
	private Double paidPrice;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne(optional = false)
	@JoinColumn(name = "CUSTOMER")
	private Customer customer;
	@JsonView({Views.Order.class, Views.Checkout.class})
	@ManyToOne(optional = true)
	@JoinColumn(name = "ADDRESS")
	private Address address;
	@JsonView({Views.Order.class, Views.Checkout.class})
	@OneToMany(mappedBy = "purchase")
	private List<PurchaseItem> purchaseItems;

//	public Double updatePrice()
//	{
//		Double price = 0.0;
//		for (PurchaseItem purchaseItem : this.purchaseItems)
//		{
//			price += purchaseItem.getInventory().getPrice() * purchaseItem.getAmount();
//		}
//		this.totalPrice = price;
//		return price;
//	}

	public enum STATUS
	{
		INITIALIZED, PLACED, PAID, PROCESSING, DISPATCHED, DELIVERED, REFUNDING, REFUNDED, CANCELLED, CLOSED
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Purchase purchase = (Purchase) o;
		return Objects.equals(id, purchase.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Purchase{" +
				"id=" + id +
				", createdAt=" + createdAt +
				", status=" + status +
				", totalPrice=" + totalPrice +
				", paidPrice=" + paidPrice +
				'}';
	}
}
//
//@Data
//class PurchaseUnfold
//{
//	private Long id;
//	private LocalDateTime createdAt;
//	private Purchase.STATUS status = Purchase.STATUS.PLACED;
//	private Double totalPrice;
//	private Double paidPrice;
//	private Address address;
//	private List<PurchaseItem> purchaseItems;
//
//
//}


