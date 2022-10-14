package com.qrzlo.webshop.data.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Data
@Entity
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

	@ManyToOne(optional = false)
	@JoinColumn(name = "PRODUCT")
	private Product product;
	@OneToMany(mappedBy = "variant")
	private Set<Attribute> attributes;
	@OneToMany(mappedBy = "variant")
	@OrderBy("createdAt ASC")
	private List<MediaFile> mediaFiles;
	@OneToMany(mappedBy = "variant", fetch = FetchType.EAGER)
	@OrderBy("price ASC")
	private Set<Inventory> inventories;
}
