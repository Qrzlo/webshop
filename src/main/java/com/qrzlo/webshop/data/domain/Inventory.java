package com.qrzlo.webshop.data.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
public class Inventory
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Min(0)
	private Integer amount;
	@NotNull
	@Min(0)
	@Column(columnDefinition = "double(10, 2)")
	private Double price;
	@NotNull
	@Column(name = "LAST_MODIFIED")
	private LocalDateTime lastModified = LocalDateTime.now();

	@ManyToOne(optional = false)
	@JoinColumn(name = "MERCHANT")
	private Merchant merchant;
	@ManyToOne(optional = false)
	@JoinColumn(name = "VARIANT")
	private Variant variant;
}
