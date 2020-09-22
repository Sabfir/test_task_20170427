package com.opinta.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.opinta.constraint.EnumString;
import com.opinta.entity.DeliveryType;
import javax.validation.constraints.Size;

import com.opinta.entity.Parcel;
import lombok.Getter;
import lombok.Setter;

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
    private List<ParcelDto> parcelDtos = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPostPay() {
        return postPay;
    }

    public void setPostPay(BigDecimal postPay) {
        this.postPay = postPay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
