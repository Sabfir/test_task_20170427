package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParcelItemDto {
    private long id;
    private String name;
    private int quantity;
    private float weight;
    private float price;
}
