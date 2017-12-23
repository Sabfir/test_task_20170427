package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Slf4j
public class Parcel {
    @Id
    @GeneratedValue
    private long id;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
    @OneToMany(mappedBy = "parcel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ParcelItem> items = new ArrayList<>();

    public Parcel(float weight, float length, float width, float height, BigDecimal declaredPrice, BigDecimal price) {
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.declaredPrice = declaredPrice;
        this.price = price;
    }

    public void addItem(ParcelItem item) {
        items.add(item);
        item.setParcel(this);
    }

    public boolean removeItem(ParcelItem item) {
        if (!items.contains(item)) {
            log.debug("Can't remove item. Item doesn't exist {}", item);
            return false;
        }
        items.remove(item);
        item.setParcel(null);
        return true;
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "id=" + id +
                ", weight=" + weight +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", declaredPrice=" + declaredPrice +
                ", price=" + price +
                '}';
    }

    public List<ParcelItem> getItems() {
        return items;
    }

    public void setItems(List<ParcelItem> items) {
        this.items.clear();
        this.items.addAll(items);
    }
}
