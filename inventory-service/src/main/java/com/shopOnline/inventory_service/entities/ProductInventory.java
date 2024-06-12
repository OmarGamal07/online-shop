package com.shopOnline.inventory_service.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product_inventory")
public class ProductInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer productCode;

    @NotNull
    private Integer quantity;

    @Min(value = 0, message = "Reorder threshold must be greater than or equal to 0")
    private Integer reorderThreshold;

    private Integer reservedQuantity = 0;
}

