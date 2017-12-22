package com.opinta.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

@Entity
@Data
@NoArgsConstructor
@Slf4j
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
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL)
    private List<Parcel> parcels = new ArrayList();
    private BigDecimal price;
    private BigDecimal postPay;
    private String description;

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType, BigDecimal postPay) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.postPay = postPay;
    }

    public void addParcel(Parcel parcel) {
        parcels.add(parcel);
        parcel.setShipment(this);
    }

    public boolean removeParcel(Parcel parcel) {
        if (!parcels.contains(parcel)) {
            log.debug("Can't remove parcel. Parcel doesn't exist {}", parcel);
            return false;
        }
        parcels.remove(parcel);
        parcel.setShipment(null);
        return true;
    }
}
