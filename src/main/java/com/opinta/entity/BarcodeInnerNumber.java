package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
public class BarcodeInnerNumber {
    @Id
    @GeneratedValue
    private long id;
    @Size(min = 7, max = 7)
    private String number;
    @Enumerated(EnumType.STRING)
    private BarcodeStatus status;

    public BarcodeInnerNumber(String number, BarcodeStatus status) {
        this.number = number;
        this.status = status;
    }
}
