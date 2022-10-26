package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Variant
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private Boolean singular;
	@Size(max = 10000)
	@Column(name = "DESCRIPTION")
	private String extraDescription;
	@NotNull
	@Min(0)
	@Column(name = "REFERENCE_PRICE")
	private Double referencePrice;
	// use AOP for VariantAPI:
	// assume that after one tx, only one or zero variant can exist for a product
	// then after each update/create/delete of the variants, use AOP
	// to see whether the singular variant has been changed, if so, change
	// the default price of the product as well.

	@ManyToOne(optional = false)
	@JoinColumn(name = "PRODUCT")
	private Product product;
	@OneToMany(mappedBy = "variant")
	private Set<Attribute> attributes;
	@OneToMany(mappedBy = "variant")
	@OrderBy("createdAt ASC")
	private List<MediaFile> mediaFiles;
	@OneToMany(mappedBy = "variant")
	@OrderBy("price ASC")
	private Set<Inventory> inventories;

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Variant variant = (Variant) o;
		return Objects.equals(id, variant.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Variant{" +
				"id=" + id +
				", singular=" + singular +
				", extraDescription='" + extraDescription + '\'' +
				", referencePrice=" + referencePrice +
				'}';
	}
}
