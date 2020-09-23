package com.opinta.service;

import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;

import java.math.BigDecimal;
import java.util.List;

public interface ParcelService {

    List<Parcel> getAll();

    Parcel getById(long id);

    Parcel save(Parcel parcel, long shipmentId);

    Parcel update(long id, long shipmentId, Parcel parcel);

    boolean delete(long id);

    BigDecimal calculatePrice(Parcel parcel, Shipment shipment);
}
