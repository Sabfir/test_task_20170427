package com.opinta.dto;


import com.opinta.entity.ParcelItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class ParcelDto {
    private long id;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private Set<ParcelItem> parcelItems;
    private BigDecimal price;
}
