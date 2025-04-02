package com.inventory.inventory.domain.model;

import lombok.Data;

@Data
public class UpdateItemDto {
    
    private String name;
    private Double price;
    private Double existence;
    
}
