package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ParcelDto {
    private long id;
    private Float weight;
    private Float length;
    private Float width;
    private Float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    private List<ParcelItemDto> parcelItems;
}
