package com.opinta.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParcelItemDto {
	private long parcelItemId;
	private String name;
	private int quantity;
	private float weight;
	private BigDecimal price;
}
