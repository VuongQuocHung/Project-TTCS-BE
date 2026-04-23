package com.laptopshop.service;

import com.laptopshop.dto.ReviewDTO;
import com.laptopshop.entity.OrderStatus;
import com.laptopshop.entity.Product;
import com.laptopshop.entity.Review;
import com.laptopshop.entity.User;
import com.laptopshop.exception.ResourceNotFoundException;
import com.laptopshop.repository.OrderRepository;
import com.laptopshop.repository.ProductRepository;
import com.laptopshop.repository.ReviewRepository;
import com.laptopshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewDTO createReview(Long userId, ReviewDTO dto) {
        // 1. Validate purchase
        boolean hasPurchased = orderRepository.existsByUserIdAndItems_Variant_ProductIdAndStatus(
                userId, dto.getProductId(), OrderStatus.DELIVERED);
        
        if (!hasPurchased) {
            throw new RuntimeException("You can only review products you have purchased and received.");
        }

        // 2. Validate "1 review per product"
        if (reviewRepository.findByUserIdAndProductId(userId, dto.getProductId()).isPresent()) {
            throw new RuntimeException("You have already reviewed this product.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(dto.getRating())
                .content(dto.getContent())
                .build();

        return convertToDTO(reviewRepository.save(review));
    }

    public List<ReviewDTO> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this review");
        }

        reviewRepository.delete(review);
    }

    private ReviewDTO convertToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .username(review.getUser().getUsername())
                .fullName(review.getUser().getFullName())
                .productId(review.getProduct().getId())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
