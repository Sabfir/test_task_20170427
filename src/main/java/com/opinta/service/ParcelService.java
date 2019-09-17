package com.opinta.service;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;

import java.util.List;

public interface ParcelService {

    List<Parcel> getAllEntities();

    Parcel getEntityById(long id);

    Parcel saveEntity(Parcel parcel);

    List<ShipmentDto> getAll();

    List<ShipmentDto> getAllByClientId(long clientId);

    ShipmentDto getById(long id);

    ShipmentDto save(ShipmentDto shipmentDto);

    ShipmentDto update(long id, ShipmentDto shipmentDto);

    boolean delete(long id);
}
