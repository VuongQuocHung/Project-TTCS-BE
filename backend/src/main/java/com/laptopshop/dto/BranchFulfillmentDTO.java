package com.laptopshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchFulfillmentDTO {
    private Long branchId;
    private String branchName;
    private String address;
    private String phone;
    private FulfillmentStatus status;
    private List<ItemFulfillmentDTO> items;
}
