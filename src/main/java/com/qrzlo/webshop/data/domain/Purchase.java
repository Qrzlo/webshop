package com.qrzlo.webshop.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Purchase
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();
	@NotNull
	@Size(max = 50)
	private String status;
	@NotNull
	@Min(0)
	@Column(name = "TOTAL_PRICE", columnDefinition = "double(10, 2)")
	private Double totalPrice;
	@NotNull
	@Min(0)
	@Column(name = "PAID_PRICE", columnDefinition = "double(10, 2)")
	private Double paidPrice;

	@ManyToOne(optional = false)
	@JoinColumn(name = "CUSTOMER")
	private Customer customer;
	@ManyToOne(optional = false)
	@JoinColumn(name = "ADDRESS")
	private Address address;
	@OneToMany(mappedBy = "purchase", fetch = FetchType.EAGER)
	private List<PurchaseItem> purchaseItems;
}
