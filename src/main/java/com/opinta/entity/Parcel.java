package com.opinta.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Parcel implements Serializable {

    @Id
    @GeneratedValue
    private long id;
    private float weight;
    private float length;
    private float width;
    private float height;
    @NotNull
    private BigDecimal price;
    private BigDecimal declaredPrice;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "parcel_id")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ParcelItem> parcelItems;

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    @JsonBackReference
    @ToString.Exclude
    private Shipment shipment;

    public Parcel(float weight, float length, BigDecimal price, BigDecimal declaredPrice) {
        this.weight = weight;
        this.length = length;
        this.price = price;
        this.declaredPrice = declaredPrice;
    }
}
