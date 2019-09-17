package com.opinta.dto;

import com.opinta.constraint.EnumString;
import com.opinta.entity.DeliveryType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ShipmentDto {
    private long id;
    private long senderId;
    private long recipientId;
    @EnumString(source = DeliveryType.class)
    private DeliveryType deliveryType;
    private BigDecimal price;
    private BigDecimal postPay;
    @Size(max = 255)
    private String description;
    private List<ParcelDto> parcels;
}
