package com.laptopshop.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    @NotNull
    private Long branchId;

    @NotEmpty
    private List<OrderItemRequest> items;

    private String voucherCode;
    private String paymentMethod;
}
