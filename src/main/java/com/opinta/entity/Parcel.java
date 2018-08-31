package com.opinta.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
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
	@OneToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "parcel_id")
	private List<ParcelItem> parcelItems;

	public Parcel(float weight, float length, float width, float height, BigDecimal declaredPrice,
			List<ParcelItem> parcelItems) {
		this.weight = weight;
		this.length = length;
		this.width = width;
		this.height = height;
		this.declaredPrice = declaredPrice;
		this.parcelItems = parcelItems;
	}
}
