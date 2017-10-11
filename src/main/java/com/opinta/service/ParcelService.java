package com.opinta.service;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;

import java.util.List;

public interface ParcelService {

    List<Parcel> getAllEntities();

    Parcel getEntityById(long id);

    List<ParcelDto> getAll();

    ParcelDto getById(long id);

    boolean delete(long id);

    void fillFields(Shipment shipment, Parcel parcel);
}
