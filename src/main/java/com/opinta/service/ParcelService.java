package com.opinta.service;

import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;

import java.math.BigDecimal;
import java.util.List;

public interface ParcelService {

    List<Parcel> getAllByShipment(Shipment shipment);

    BigDecimal calculatePrice(Parcel parcel);
}
