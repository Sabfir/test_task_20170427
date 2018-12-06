package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue
    private long id;
    private String postcode;
    private String region;
    private String district;
    private String city;
    private String street;
    private String houseNumber;
    private String apartmentNumber;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public Address(String postcode, String region, String district, String city, String street, String houseNumber,
                   String apartmentNumber) {
        this.postcode = postcode;
        this.region = region;
        this.district = district;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.apartmentNumber = apartmentNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }
}
