package com.opinta.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ListIndexBase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.OrderColumn;
import javax.persistence.FetchType;
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

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    @JsonIgnore
    private Shipment shipment;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = ParcelItem.class, mappedBy = "parcel", fetch = FetchType.EAGER)
    @OrderColumn
    @ListIndexBase
    private List<ParcelItem> parcelItems;

    public Parcel(List<ParcelItem> parcelItems, float weight,
                  float length, float width,
                  float height, BigDecimal declaredPrice, BigDecimal price) {
        this.parcelItems = parcelItems;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.declaredPrice = declaredPrice;
        this.price = price;
    }
}
