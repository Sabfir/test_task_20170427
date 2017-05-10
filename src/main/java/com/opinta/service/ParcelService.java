package com.opinta.service;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;

import java.math.BigDecimal;
import java.util.List;


public interface ParcelService {
    BigDecimal calculatePrice(Parcel parcel, Shipment shipment);
    List<Parcel> getAllEntities();

    Parcel getEntityById(long id);

    Parcel saveEntity(Parcel parcel);

    List<ParcelDto> getAll();

    ParcelDto getById(long id);

    ParcelDto save(ParcelDto parcelDto);

    ParcelDto update(long id, ParcelDto parcelDto);

}
