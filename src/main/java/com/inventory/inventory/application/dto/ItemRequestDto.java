package com.inventory.inventory.application.dto;

import lombok.Data;

@Data
public class ItemRequestDto {
    private String name;

    private String code;

    private double price;

    private Integer existence;
}
