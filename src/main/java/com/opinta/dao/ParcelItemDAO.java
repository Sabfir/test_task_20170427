package com.opinta.dao;

import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;

import java.util.List;

public interface ParcelItemDAO {

    List<ParcelItem> getAll();

    List<ParcelItem> getAllByParcel(Parcel parcel);

    ParcelItem getById(long id);

    ParcelItem save(ParcelItem item);

    void update(ParcelItem item);

    void delete(ParcelItem item);
}
