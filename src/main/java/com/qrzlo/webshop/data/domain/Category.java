package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Category
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Size(min = 1, max = 250)
	private String name;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne
	private Category parent;
	// question: find a category c, c is managed. But are products also managed?
	// c.products.remove(some products) .add(other products)
	// this new Set of products automatically flushed into the DB?

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parent")
	private Set<Category> children = new HashSet<>();

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Category category = (Category) o;
		return Objects.equals(id, category.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Category{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
