package com.opinta.service;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;

import java.math.BigDecimal;
import java.util.List;

public interface ParcelService {

    List<ParcelDto> getAll();

    List<ParcelDto> getAllByShipment(Shipment shipment);

    ParcelDto getById(long id);

    BigDecimal calculatePrice(Parcel parcel);
}
