package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Purchase
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@NotNull
	private STATUS status = STATUS.PLACED;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@NotNull
	@Min(0)
	@Column(name = "TOTAL_PRICE", columnDefinition = "double(10, 2)")
	private Double totalPrice;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@NotNull
	@Min(0)
	@Column(name = "PAID_PRICE", columnDefinition = "double(10, 2)")
	private Double paidPrice;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne(optional = false)
	@JoinColumn(name = "CUSTOMER")
	private Customer customer;
	@ManyToOne
	@JoinColumn(name = "ADDRESS")
	private Address address;
	@OneToMany(mappedBy = "purchase")
	private List<PurchaseItem> purchaseItems;

	public Double updatePrice()
	{
		Double price = 0.0;
		for (PurchaseItem purchaseItem : this.purchaseItems)
		{
			price += purchaseItem.getInventory().getPrice();
		}
		this.totalPrice = price;
		return price;
	}

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


