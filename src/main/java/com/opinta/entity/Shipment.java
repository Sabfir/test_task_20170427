package com.opinta.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
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
    @OneToMany(mappedBy = "shipmentId", cascade = CascadeType.ALL)
    private List<Parcel> parcelList = new ArrayList<>();

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType, BigDecimal price, BigDecimal postPay) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.price = price;
        this.postPay = postPay;
    }

    public Shipment() {
        super();
    }

    public long getId() {
        return id;
    }

    public Client getSender() {
        return sender;
    }

    public Client getRecipient() {
        return recipient;
    }

    public BarcodeInnerNumber getBarcode() {
        return barcode;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getPostPay() {
        return postPay;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSender(Client sender) {
        this.sender = sender;
    }

    public void setRecipient(Client recipient) {
        this.recipient = recipient;
    }

    public void setBarcode(BarcodeInnerNumber barcode) {
        this.barcode = barcode;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setPostPay(BigDecimal postPay) {
        this.postPay = postPay;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParcelList(List<Parcel> parcelList) {
        this.parcelList = parcelList;
    }

    public List<Parcel> getParcelList() {
        return parcelList;
    }

    public boolean addParcelToList(Parcel parcel) {
        if (parcel.getShipment() == null) {
            parcel.setShipment(this);
            parcelList.add(parcel);
            price.add(parcel.getPrice());
            return true;
        }
        return false;
    }
}
