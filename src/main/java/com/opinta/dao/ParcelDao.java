package com.opinta.dao;

import com.opinta.entity.Parcel;

import java.util.List;

public interface ParcelDao {

    List<Parcel> getAll();

    Parcel getById(long id);

    Parcel save(Parcel address);

    void update(Parcel address);

    void delete(Parcel address);
}
