package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "parcel_id", nullable = false)
    @OrderColumn(name = "parcel_position", nullable = false)
    private List<Parcel> parcels;

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType, BigDecimal postPay, List<Parcel> parcels) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.postPay = postPay;
        this.parcels = parcels;
    }

    public void setParcels(List<Parcel> parcels) {
        if (this.parcels == null) {
            this.parcels = parcels;
        } else {
            this.parcels.clear();
            this.parcels.addAll(parcels);
        }
    }
}
