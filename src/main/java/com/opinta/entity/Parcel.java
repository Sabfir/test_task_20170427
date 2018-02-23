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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    @OrderColumn(name = "item_position", nullable = false)
    private List<ParcelItem> items;

    public Parcel(float weight, float length, float width, float height, BigDecimal declaredPrice,
                  BigDecimal price, List<ParcelItem> items) {
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.declaredPrice = declaredPrice;
        this.price = price;
        this.items = items;
    }

    public void setItems(List<ParcelItem> items) {
        if (this.items == null) {
            this.items = items;
        } else {
            this.items.clear();
            this.items.addAll(items);
        }
    }
}
