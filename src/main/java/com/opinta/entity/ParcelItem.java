package com.opinta.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ParcelItem {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private int quantity;
    private float weight;
    private BigDecimal price;
    @ManyToMany
    @JoinTable(
            name = "ParselItem_Parsel",
            joinColumns = {@JoinColumn(name = "ParcelItem_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "Parcel_id", referencedColumnName = "id")})
    private List<Parcel> parcels = new ArrayList<>();

    public ParcelItem(long id) {
        this.id = id;
    }

    public ParcelItem() {
        super();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getWeight() {
        return weight;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public List<Parcel> getParcels() {
        return parcels;
    }

    public void setParcels(List<Parcel> parcels) {
        this.parcels = parcels;
    }
}
