package com.opinta.entity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private BigDecimal price;
    private BigDecimal postPay;
    private String description;
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name = "shipment_id")
    private List<Parcel> parcels;

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType,
                    BigDecimal postPay, List<Parcel> parcels) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.postPay = postPay;
        this.parcels = parcels;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Client getSender() {
        return sender;
    }

    public void setSender(Client sender) {
        this.sender = sender;
    }

    public Client getRecipient() {
        return recipient;
    }

    public void setRecipient(Client recipient) {
        this.recipient = recipient;
    }

    public BigDecimal getPostPay() {
        return postPay;
    }

    public float getWeight() {
        return (float) parcels.stream().mapToDouble(Parcel::getWeight).sum();
    }

    public float getLength() {
        return (float) parcels.stream().mapToDouble(Parcel::getLength).sum();
    }

    public BigDecimal getPrice() {
        List<BigDecimal> prices = parcels.stream().map(Parcel::getPrice).collect(Collectors.toList());
        return prices.stream().reduce(BigDecimal::add).orElseGet(() -> BigDecimal.ZERO);
    }

    public BigDecimal getDeclaredPrice() {
        List<BigDecimal> prices = parcels.stream().map(Parcel::getDeclaredPrice).collect(Collectors.toList());
        return prices.stream().reduce(BigDecimal::add).orElseGet(() -> BigDecimal.ZERO);
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setBarcode(BarcodeInnerNumber barcode) {
        this.barcode = barcode;
    }
}
