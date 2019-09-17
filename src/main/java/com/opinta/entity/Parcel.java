package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Parcel {

    @Id
    @GeneratedValue
    private long id;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parcel_id")
    private List<ParcelItem> parcelItems;

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    public Parcel(float weight, float length, float width, float height, BigDecimal declaredPrice, BigDecimal price) {
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.declaredPrice = declaredPrice;
        this.price = price;
    }
}
