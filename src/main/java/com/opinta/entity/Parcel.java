package com.opinta.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Parcel {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
    @ManyToMany(mappedBy = "parcels", cascade = CascadeType.ALL)
    List<ParcelItem> parcelItems = new ArrayList<>();
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;

    public Parcel(float weight, float length, float width, float height, BigDecimal declaredPrice, BigDecimal price) {
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.declaredPrice = declaredPrice;
        this.price = price;
    }

    public Parcel() {
        super();
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public BigDecimal getDeclaredPrice() {
        return declaredPrice;
    }

    public void setDeclaredPrice(BigDecimal declaredPrice) {
        this.declaredPrice = declaredPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<ParcelItem> getParcelItems() {
        return Collections.unmodifiableList(parcelItems);
    }

    public void addParcelItemToParcels(ParcelItem parcelItem) {
        if (!parcelItems.contains(parcelItem)) parcelItems.add(parcelItem);
        if (!parcelItem.getParcels().contains(this)) {
            List<Parcel> parcels = parcelItem.getParcels();
            parcels.add(this);
            parcelItem.setParcels(parcels);
        }
    }
}
