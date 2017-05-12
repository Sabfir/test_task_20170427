package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Мария on 09.05.2017.
 */
@Getter
@Setter
public class ParcelItemDto {
    private long id;
    private String name;
    private float quantity;
    private float weight;
    private BigDecimal price;

}
