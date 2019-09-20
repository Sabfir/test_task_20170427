package com.opinta.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
@Entity
@Data
@NoArgsConstructor
public class Shipment implements Serializable {
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
    private BigDecimal price = BigDecimal.ZERO;
    private BigDecimal postPay = BigDecimal.ZERO;
    private String description;

    @OneToMany(mappedBy = "shipment", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<Parcel> parcelList = new ArrayList<>();

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType, BigDecimal postPay) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.postPay = postPay;
    }

//    public void setParcelList(List<Parcel> parcelList) {
////        if (parcelList != null && !parcelList.isEmpty()) {
////            parcelList.forEach((parcel) -> {
////                this.price = this.price.add(parcel == null ? BigDecimal.ZERO : parcel.getPrice());
////            });
////        }
//        this.parcelList = parcelList;
//    }
}
