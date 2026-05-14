package com.laptopshop.controller.me;

import com.laptopshop.dto.ReviewDTO;
import com.laptopshop.security.SecurityUtils;
import com.laptopshop.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "Quản lý đánh giá sản phẩm của tôi")
public class MeReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewDTO createReview(@RequestBody ReviewDTO dto) {
        return reviewService.createReview(SecurityUtils.getCurrentUserId(), dto);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(SecurityUtils.getCurrentUserId(), id);
    }
}
