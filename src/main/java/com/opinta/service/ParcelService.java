package com.opinta.service;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.W2wVariation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface ParcelService {

    List<Parcel> getAllEntities();

    Parcel getEntityById(long id);

    Parcel saveEntity(Parcel parcel);

    List<ParcelDto> getAll();

    List<ParcelDto> getAllByShipmentId(long shipmentId);

    ParcelDto getById(long id);

    ParcelDto save(ParcelDto parcelDto);

    ParcelDto update(long id, ParcelDto parcelDto);

    boolean delete(long id);

    BigDecimal calculateParcelsPrice(List<Parcel> parcels, W2wVariation w2wVariation);
}
