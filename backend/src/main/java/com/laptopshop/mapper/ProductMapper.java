package com.laptopshop.mapper;

import com.laptopshop.dto.InventoryDTO;
import com.laptopshop.dto.ProductDTO;
import com.laptopshop.dto.ProductVariantDTO;
import com.laptopshop.entity.Inventory;
import com.laptopshop.entity.Product;
import com.laptopshop.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "brandName", source = "brand.name")
    ProductDTO toDto(Product product);

    @Mapping(target = "inventories", source = "stocks")
    @Mapping(target = "quantity", expression = "java(calculateTotalQuantity(variant.getStocks()))")
    ProductVariantDTO toDto(ProductVariant variant);

    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    InventoryDTO toDto(Inventory inventory);

    default Integer calculateTotalQuantity(List<Inventory> stocks) {
        if (stocks == null) return 0;
        return stocks.stream().mapToInt(Inventory::getQuantity).sum();
    }
}
