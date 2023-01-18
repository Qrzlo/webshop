package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Customer;
import com.qrzlo.webshop.data.domain.Review;
import com.qrzlo.webshop.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/review",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class ReviewAPI
{
	private ReviewService reviewService;

	public ReviewAPI(ReviewService reviewService)
	{
		this.reviewService = reviewService;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Review review,
									@AuthenticationPrincipal Customer customer)
	{
		var newReview = reviewService.create(review, customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
	}

	@GetMapping
	public ResponseEntity<?> read(@RequestParam(name = "product") Integer productId)
	{
		var reviews = reviewService.read(productId);
		return ResponseEntity.ok(reviews);
	}
}
