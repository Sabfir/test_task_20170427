package com.opinta.dao;

import com.opinta.entity.Parcel;

import java.util.List;

/**
 * Created by Dmytro Kushnir on 13.05.17.
 */
public interface ParcelDao {

    List<Parcel> getAll(long shipmentId);

    Parcel getById(long id);

    Parcel save(Parcel parcel);

    void update(Parcel parcel);

    void delete(Parcel parcel);

}
