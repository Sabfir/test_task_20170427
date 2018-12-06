package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

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
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name = "parcel_id")
    private List<ParcelItem> parcelItems;

    public Parcel(float weight, float length, BigDecimal declaredPrice, BigDecimal price,
                  List<ParcelItem> parcelItems) {
        this.weight = weight;
        this.length = length;
        this.declaredPrice = declaredPrice;
        this.price = price;
        this.parcelItems = parcelItems;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getDeclaredPrice() {
        return declaredPrice;
    }
}
