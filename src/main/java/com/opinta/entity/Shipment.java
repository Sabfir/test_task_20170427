package com.opinta.entity;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@Data
@NoArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Client sender;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private Client recipient;
    @OneToOne
    private BarcodeInnerNumber barcode;
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;
    private BigDecimal price;
    private BigDecimal postPay;
    private String description;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "parcel_id")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Parcel> parcels;

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType,
                    BigDecimal postPay, List<Parcel> parcels) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.postPay = postPay;
        this.parcels = parcels;
    }

    public float calculateTotalWeight(List<Parcel> parcels) {
        return (float) parcels.stream()
                .mapToDouble(Parcel::getWeight)
                .sum();
    }

    public BigDecimal calculateTotalDeclaredPrice(List<Parcel> parcels) {
        double sum = 0.0;
        for (Parcel parcel : parcels) {
            double v = Double.valueOf(parcel.getDeclaredPrice().toString());
            sum += v;
        }
        return BigDecimal.valueOf(sum);
    }

    public BigDecimal calculateTotalPrice() {
        return BigDecimal.valueOf(parcels.stream()
                .mapToDouble(parcel -> Double.valueOf(parcel.getPrice().toString()))
                .sum());
    }
}
