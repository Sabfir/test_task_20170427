package com.opinta.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Client sender;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private Client recipient;
    @OneToOne
    private BarcodeInnerNumber barcode;
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;
    private BigDecimal postPay;
    private String description;
    private BigDecimal price;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL)
    private List<Parcel> parcelSet;

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType, BigDecimal postPay) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.price = BigDecimal.ZERO;
        this.postPay = postPay;
    }

    public void setParcelSet(List<Parcel> parcelSet) {
        parcelSet.forEach((parcel) -> price = price.add(parcel == null ? BigDecimal.ZERO : parcel.getPrice()));
        this.parcelSet = parcelSet;
    }
}
