package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ParcelItemDto {

    private long id;
    private String name;
    private Long quantity;
    private float weight;
    private BigDecimal price;
}
