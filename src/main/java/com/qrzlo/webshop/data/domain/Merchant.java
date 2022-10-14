package com.qrzlo.webshop.data.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
public class Merchant
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Size(min = 1, max = 200)
	private String email;
	@NotNull
	@Size(min = 1, max = 100)
	private String brand;
	@NotNull
	@Size(min = 1, max = 255)
	private String password;
	@NotNull
	@Size(max = 10000)
	private String description;
	@Size(max = 50)
	private String delivery;
	@NotNull
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();
}
