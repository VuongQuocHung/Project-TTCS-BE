package com.laptopshop.controller.pub;

import com.laptopshop.dto.ReviewDTO;
import com.laptopshop.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "Xem đánh giá sản phẩm (Reviews)")
public class PublicReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public List<ReviewDTO> getProductReviews(@PathVariable Long productId) {
        return reviewService.getProductReviews(productId);
    }
}
