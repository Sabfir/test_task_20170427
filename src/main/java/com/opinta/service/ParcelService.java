package com.opinta.service;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import java.util.List;

/**
 * Created by Dmytro Kushnir on 13.05.17.
 */
public interface ParcelService {

    List<Parcel> getAllEntities(long shipmentId);

    Parcel getEntityById(long id);

    Parcel saveEntity(Parcel parcel);

    List<ParcelDto> getAll(long shipmentId);

    ParcelDto getById(long id);

    ParcelDto save(long shipmentId, ParcelDto parcelDto);

    ParcelDto update(long shipmentId, long id, ParcelDto parcelDto);

    boolean delete(long shipmentId, long id);

    List<Parcel> calculatePrices(Shipment shipment);

}
