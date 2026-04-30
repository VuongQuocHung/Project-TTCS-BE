package com.laptopshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private String username;
    private String fullName;
    private Long productId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}
