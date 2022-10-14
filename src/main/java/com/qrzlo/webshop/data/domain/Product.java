package com.qrzlo.webshop.data.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "product")
public class Product
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Size(min = 1, max = 200)
	private String name;
	@Size(max = 10000)
	private String description;
	@NotNull
	@Min(0)
	private Double referencePrice;
	@NotNull
	@Column(name = "FOR_SALE_FROM")
	private LocalDateTime forSaleFrom = LocalDateTime.now();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	private Set<Variant> variants;
	@ManyToMany()
	@JoinTable(name = "PRODUCT_CATEGORY",
			joinColumns = @JoinColumn(name = "CATEGORY"),
			inverseJoinColumns = @JoinColumn(name = "PRODUCT"))
	private Set<Category> categories;
	@ManyToOne
	@JoinColumn(name = "SERIES")
	private Series series;
	@OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
	private Set<Dimension> dimensions;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	@OrderBy("createdAt ASC")
	private List<Review> reviews;
}
