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
public class ParcelItem {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private Long quantity;
    private float weight;
    private BigDecimal price;

}
