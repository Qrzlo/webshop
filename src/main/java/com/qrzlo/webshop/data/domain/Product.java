package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@Column(name = "FOR_SALE_FROM")
	private LocalDateTime forSaleFrom = LocalDateTime.now();
	@Min(0)
	@Column(name = "DEFAULT_PRICE")
	private Double defaultPrice;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	private Set<Variant> variants;
	@JsonIgnore
	@ManyToMany()
	@JoinTable(name = "PRODUCT_CATEGORY",
			joinColumns = @JoinColumn(name = "CATEGORY"),
			inverseJoinColumns = @JoinColumn(name = "PRODUCT"))
	private Set<Category> categories;
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "SERIES")
	private Series series;
	@JsonIgnore
	@OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
	private Set<Dimension> dimensions;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	@OrderBy("createdAt ASC")
	private List<Review> reviews;
}
