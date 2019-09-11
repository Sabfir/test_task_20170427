package com.opinta.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "shipment_id")
    @Fetch(FetchMode.SUBSELECT)
    private List<Parcel> parcels = new ArrayList<>();

    private BigDecimal price;
    private BigDecimal postPay;
    private String description;

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType, BigDecimal postPay, Parcel... parcels) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.postPay = postPay;
        if (parcels.length > 0) {
            this.parcels.addAll(Arrays.asList(parcels));
        }
    }

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType, BigDecimal postPay, List<Parcel> parcels) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.postPay = postPay;
        this.parcels = new ArrayList<>(parcels);
    }
}
