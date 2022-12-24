package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.qrzlo.webshop.data.Views;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MediaFile
{
	@JsonView(Views.Product.class)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonView({Views.Product.class, Views.Basket.class, Views.Catalog.class, Views.Purchase.class})
	@NotNull
	@Size(min = 10, max = 300)
	private String url;
	@NotNull
	private LocalDateTime createdAt = LocalDateTime.now();

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne(optional = false)
	@JoinColumn(name = "VARIANT")
	private Variant variant;

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MediaFile mediaFile = (MediaFile) o;
		return Objects.equals(id, mediaFile.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "MediaFile{" +
				"id=" + id +
				", url='" + url + '\'' +
				", createdAt=" + createdAt +
				'}';
	}
}
