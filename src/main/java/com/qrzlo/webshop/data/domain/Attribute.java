package com.qrzlo.webshop.data.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
public class Attribute
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 1, max = 50)
	private String value;

	@ManyToOne(optional = false)
	@JoinColumn(name = "variant")
	private Variant variant;
	@ManyToOne(optional = false)
	@JoinColumn(name = "DIMENSION")
	private Dimension dimension;
}
