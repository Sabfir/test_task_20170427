package com.opinta.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class ParcelItem implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private int quantity;
    private float weight;
    private BigDecimal price;

    public ParcelItem(String name, int quantity, float weight, BigDecimal price) {
        this.name = name;
        this.quantity = quantity;
        this.weight = weight;
        this.price = price;
    }
}

