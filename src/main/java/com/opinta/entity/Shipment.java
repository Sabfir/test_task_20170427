package com.opinta.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.OrderColumn;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ListIndexBase;

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

    private BigDecimal postPay;
    private String description;
    private BigDecimal price;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = Parcel.class, mappedBy = "shipment", fetch = FetchType.EAGER)
    @OrderColumn
    @ListIndexBase
    private List<Parcel> parcels;

    public Shipment(Client sender, Client recipient,
                    DeliveryType deliveryType,
                    BigDecimal postPay, List<Parcel> parcels) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.postPay = postPay;
        this.parcels = parcels;
    }
}
