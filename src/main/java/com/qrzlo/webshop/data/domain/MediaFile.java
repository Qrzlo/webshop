package com.qrzlo.webshop.data.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
public class MediaFile
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 10, max = 300)
	private String url;
	@NotNull
	private LocalDateTime createdAt = LocalDateTime.now();

	@ManyToOne(optional = false)
	@JoinColumn(name = "VARIANT")
	private Variant variant;
}
