package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
public class Series
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Size(min = 1, max = 100)
	private String name;
	@Size(max = 10000)
	private String description;
	@NotNull
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "series")
	private Set<Product> products;

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Series series = (Series) o;
		return Objects.equals(id, series.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Series{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", createdAt=" + createdAt +
				'}';
	}
}
