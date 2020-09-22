package com.opinta.dto;

import com.opinta.entity.Shipment;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ParcelDto {
    private long id;
    private Shipment shipment;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
}
