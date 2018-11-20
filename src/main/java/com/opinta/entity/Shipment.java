package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    private BigDecimal declaredPrice;
    private BigDecimal price;
    private BigDecimal postPay;
    private String description;
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Parcel> parcels = new ArrayList<>();

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType,
                    BigDecimal declaredPrice, BigDecimal price, BigDecimal postPay) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.declaredPrice = declaredPrice;
        this.price = price;
        this.postPay = postPay;
    }

    public void addParcel(Parcel parcel) {
        parcels.add(parcel);
        parcel.setShipment(this);
    }

    public void removeParcel(Parcel parcel) {
        parcels.remove(parcel);
        parcel.setShipment(null);
    }
}
