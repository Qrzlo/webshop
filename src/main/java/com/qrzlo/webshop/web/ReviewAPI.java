package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Purchase;
import com.qrzlo.webshop.data.domain.Review;
import com.qrzlo.webshop.data.repository.ProductRepository;
import com.qrzlo.webshop.data.repository.PurchaseItemRepository;
import com.qrzlo.webshop.data.repository.PurchaseRepository;
import com.qrzlo.webshop.data.repository.ReviewRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(path = "/api/review",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class ReviewAPI
{
	private ReviewRepository reviewRepository;
	private PurchaseRepository purchaseRepository;
	private PurchaseItemRepository purchaseItemRepository;
	private ProductRepository productRepository;

	public ReviewAPI(ReviewRepository reviewRepository, PurchaseRepository purchaseRepository, PurchaseItemRepository purchaseItemRepository, ProductRepository productRepository)
	{
		this.reviewRepository = reviewRepository;
		this.purchaseRepository = purchaseRepository;
		this.purchaseItemRepository = purchaseItemRepository;
		this.productRepository = productRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Review review,
									@AuthenticationPrincipal Customer customer)
	{
		try
		{
			var product = productRepository.findById(review.getProduct().getId()).orElseThrow();
			var exist = reviewRepository.findReviewByCustomerAndProduct(customer, product);
			if (exist != null)
				throw new Exception("a review already exists");
			var purchases = purchaseRepository.findPurchasesByCustomerAndStatusIsIn(customer,
					Set.of(Purchase.STATUS.DELIVERED, Purchase.STATUS.CLOSED, Purchase.STATUS.REFUNDED));
			purchases.stream()
					.filter(p -> purchaseItemRepository.findPurchaseItemsByPurchase(p)
						.stream()
						.filter(item -> item.getInventory().getVariant().getProduct().equals(product))
						.findAny().isPresent())
					.findAny().orElseThrow();
			review.setCustomer(customer);
			var newReview = reviewRepository.save(review);
			return ResponseEntity.ok(newReview);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping
	public ResponseEntity<?> read(@RequestParam(name = "product") Integer productId)
	{
		try
		{
			var product = productRepository.findById(productId).orElseThrow();
			var reviews = reviewRepository.findReviewsByProductOrderByCreatedAt(product);
			return ResponseEntity.ok(reviews);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}
}
