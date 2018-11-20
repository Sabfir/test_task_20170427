package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

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
    private BigDecimal price;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
    @OneToMany(mappedBy = "parcel",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
            )
    @Fetch(FetchMode.SUBSELECT)
    private List<ParcelItem> parcelItems = new ArrayList<>();

    public Parcel(float weight, float length, float width, float height, BigDecimal price) {
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.price = price;
    }

    public void addParcelItem(ParcelItem parcelItem) {
        parcelItems.add(parcelItem);
        parcelItem.setParcel(this);
    }

    public void removeParcelItem(ParcelItem parcelItem) {
        parcelItems.remove(parcelItem);
        parcelItem.setParcel(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Parcel parcel = (Parcel) o;
        return Float.compare(parcel.weight, this.weight) == 0 &&
                Float.compare(parcel.length, this.length) == 0 &&
                Float.compare(parcel.width, this.width) == 0 &&
                Float.compare(parcel.height, this.height) == 0 &&
                Objects.equals(this.price, parcel.price) &&
                Objects.equals(this.shipment, parcel.shipment);
    }

    @Override
    public int hashCode() {
        return 37;
    }
}
