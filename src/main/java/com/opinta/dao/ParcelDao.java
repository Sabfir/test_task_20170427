package com.opinta.dao;

import com.opinta.entity.Parcel;

import java.util.List;

public interface ParcelDao {

    List<Parcel> getAllByShipmentId(long shipmentId);

    List<Parcel> getAll();

    Parcel getById(long id);

    Parcel save(Parcel parcel);

    void update(Parcel parcel);

    void delete(Parcel parcel);
}
