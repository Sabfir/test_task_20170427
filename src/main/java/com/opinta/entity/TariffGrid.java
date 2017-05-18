package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class TariffGrid {
    @Id
    @GeneratedValue
    private long id;
    private float weight;
    private float length;
    @Enumerated(EnumType.STRING)
    private W2wVariation w2wVariation;
    private float price;

    public TariffGrid(float weight, float length, W2wVariation w2wVariation, float price) {
        this.weight = weight;
        this.length = length;
        this.w2wVariation = w2wVariation;
        this.price = price;
    }
}
