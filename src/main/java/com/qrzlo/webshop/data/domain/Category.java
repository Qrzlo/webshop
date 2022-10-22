package com.qrzlo.webshop.data.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
public class Category
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Size(min = 1, max = 250)
	private String name;

	@ManyToOne
	private Category parent;
	// question: find a category c, c is managed. But are products also managed?
	// c.products.remove(some products) .add(other products)
	// this new Set of products automatically flushed into the DB?
}
