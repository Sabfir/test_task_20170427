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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Parcel> parcels = new ArrayList<>();

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType, BigDecimal postPay,
                    List<Parcel> parcels) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.postPay = postPay;
        this.parcels = parcels;
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "id=" + id +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", barcode=" + barcode +
                ", deliveryType=" + deliveryType +
                ", price=" + price +
                ", postPay=" + postPay +
                ", description='" + description + '\'' +
                ", parcels=" + parcels +
                '}';
    }
}
