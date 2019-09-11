package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Parcel {
    @Id
    @GeneratedValue
    private long id;
    private Float weight;
    private Float length;
    private Float width;
    private Float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "parcel_id")
    @Fetch(FetchMode.SUBSELECT)
    private List<ParcelItem> parcelItems = new ArrayList<>();

    public Parcel(Float weight, Float length, BigDecimal declaredPrice, BigDecimal price, ParcelItem... items) {
        this.weight = weight;
        this.length = length;
        this.declaredPrice = declaredPrice;
        this.price = price;
        if (items.length > 0) {
            parcelItems.addAll(Arrays.asList(items));
        }
    }

    public Parcel(Float weight, Float length, BigDecimal declaredPrice, BigDecimal price, List<ParcelItem> parcelItems) {
        this.weight = weight;
        this.length = length;
        this.declaredPrice = declaredPrice;
        this.price = price;
        this.parcelItems = new ArrayList<>(parcelItems);
    }
}
