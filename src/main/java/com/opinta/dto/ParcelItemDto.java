package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Dmytro Kushnir on 13.05.17.
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
