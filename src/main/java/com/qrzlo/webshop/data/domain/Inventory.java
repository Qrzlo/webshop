package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Inventory
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Min(0)
	private Integer amount;
	@NotNull
	@Min(0)
	@Column(columnDefinition = "double(10, 2)")
	private Double price;
	@NotNull
	@Column(name = "LAST_MODIFIED")
	private LocalDateTime lastModified = LocalDateTime.now();

	@ManyToOne(optional = false)
	@JoinColumn(name = "MERCHANT")
	private Merchant merchant;
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
