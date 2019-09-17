package com.opinta.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class ParcelItem {

    @Id
    @GeneratedValue
    private long id;
    private String name;
    private int quantity;
    private float weight;
    private BigDecimal price;

//    @ManyToOne
//    @JoinColumn(name = "parcel_id")
//    private Parcel parcel;

    public ParcelItem(String name, int quantity, float weight, BigDecimal price) {
        this.name = name;
        this.quantity = quantity;
        this.weight = weight;
        this.price = price;
    }
}
