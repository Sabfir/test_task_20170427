package com.opinta.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    @OneToMany(mappedBy = "parcel", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @Getter
    @Setter
    private List<ParcelItem> parcelItems = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    public Parcel(float weight, float length, BigDecimal declaredPrice, BigDecimal price, List<ParcelItem> parcelItems) {
        this.weight = weight;
        this.length = length;
        this.declaredPrice = declaredPrice;
        this.price = price;
        this.parcelItems = parcelItems;
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "id=" + id +
                ", price=" + price +
                ", parcelItems=" + parcelItems +
                '}';
    }
}
