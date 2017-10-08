package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Dmytro Kushnir on 13.05.17.
 */
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "parcel_item_id")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ParcelItem> parcelItems;

    public Parcel(float weight, float length, float width, float height, BigDecimal declaredPrice,
                  BigDecimal price, List<ParcelItem> parcelItems) {
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.declaredPrice = declaredPrice;
        this.price = price;
        this.parcelItems = parcelItems;
    }
}
