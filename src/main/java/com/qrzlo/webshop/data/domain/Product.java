package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.*;
import com.qrzlo.webshop.data.Views;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Product
{

	@JsonView({Views.Catalog.class, Views.Basket.class, Views.Product.class})
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonView({Views.Catalog.class, Views.Basket.class, Views.Product.class, Views.Purchase.class})
	@NotNull
	@Size(min = 1, max = 200)
	private String name;
	@JsonView(Views.Product.class)
	@Size(max = 10000)
	private String description;
	@JsonView({Views.Catalog.class, Views.Product.class})
	@NotNull
	@Column(name = "FOR_SALE_FROM")
	private LocalDateTime forSaleFrom = LocalDateTime.now();
	@JsonView({Views.Catalog.class, Views.Product.class})
	@Min(0)
	@Column(name = "DEFAULT_PRICE")
	private Double defaultPrice;

	@JsonView({Views.Catalog.class, Views.Product.class})
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@OneToMany(mappedBy = "product")
	private Set<Variant> variants;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@ManyToMany
	@JoinTable
	private Set<Category> categories;
	@JsonView(Views.Product.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@ManyToOne
	@JoinColumn(name = "SERIES")
	private Series series;
	@JsonView({Views.Basket.class, Views.Product.class, Views.Purchase.class})
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
	private Set<Dimension> dimensions;
	@JsonView(Views.Product.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	@OrderBy("createdAt ASC")
	private List<Review> reviews;

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Product product = (Product) o;
		return Objects.equals(id, product.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Product{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", forSaleFrom=" + forSaleFrom +
				", defaultPrice=" + defaultPrice +
				'}';
	}
}
