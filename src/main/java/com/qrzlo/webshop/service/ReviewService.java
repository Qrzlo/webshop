package com.qrzlo.webshop.service;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Purchase;
import com.qrzlo.webshop.data.domain.Review;
import com.qrzlo.webshop.data.repository.ProductRepository;
import com.qrzlo.webshop.data.repository.PurchaseItemRepository;
import com.qrzlo.webshop.data.repository.PurchaseRepository;
import com.qrzlo.webshop.data.repository.ReviewRepository;
import com.qrzlo.webshop.util.exception.AbsentDataException;
import com.qrzlo.webshop.util.exception.InvalidRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ReviewService
{
	private ReviewRepository reviewRepository;
	private PurchaseRepository purchaseRepository;
	private PurchaseItemRepository purchaseItemRepository;
	private ProductRepository productRepository;

	public ReviewService(ReviewRepository reviewRepository, PurchaseRepository purchaseRepository, PurchaseItemRepository purchaseItemRepository, ProductRepository productRepository)
	{
		this.reviewRepository = reviewRepository;
		this.purchaseRepository = purchaseRepository;
		this.purchaseItemRepository = purchaseItemRepository;
		this.productRepository = productRepository;
	}

	/**
	 * Not invoked by the frontend yet
	 * @param review
	 * @param customer
	 * @return
	 */
	public Review create(Review review, Customer customer)
	{
		var product = productRepository.findById(review.getProduct().getId())
				.orElseThrow(() -> new AbsentDataException("The product cannot be found"));
		reviewRepository.findReviewByCustomerAndProduct(customer, product)
				.ifPresent(r -> {
					throw new InvalidRequestException("A review already existed");
				});
		var purchases = purchaseRepository.findPurchasesByCustomerAndStatusIsIn(customer,
				Set.of(Purchase.STATUS.PLACED, Purchase.STATUS.DELIVERED, Purchase.STATUS.CLOSED, Purchase.STATUS.REFUNDED));
		purchases.stream()
				.filter(p -> purchaseItemRepository
						.findPurchaseItemsByPurchase(p)
						.stream()
						.filter(item -> item.getInventory().getVariant().getProduct().equals(product))
						.findAny().isPresent())
				.findAny()
				.orElseThrow(() -> new InvalidRequestException("No associated purchase has been found"));
		review.setCustomer(customer);
		return reviewRepository.save(review);
	}

	public List<Review> read(Integer productId)
	{
		var product = productRepository.findById(productId).orElseThrow();
		return reviewRepository.findReviewsByProductOrderByCreatedAt(product);
	}
}
