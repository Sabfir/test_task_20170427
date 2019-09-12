package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ParcelItemDto {
    private long id;
    private String name;
    private Integer quantity;
    private Float weight;
    private BigDecimal price;

}
