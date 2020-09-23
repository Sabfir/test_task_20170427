package com.opinta.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
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
    private float width;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "parcel_id")
    @JsonIgnore
    private Parcel parcel;

    public ParcelItem(String name, int quantity, float width, BigDecimal price) {
        this.name = name;
        this.quantity = quantity;
        this.width = width;
        this.price = price;
    }
}
