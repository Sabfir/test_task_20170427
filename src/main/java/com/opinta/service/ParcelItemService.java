package com.opinta.service;


import com.opinta.entity.ParcelItem;

import java.util.List;

public interface ParcelItemService {

    List<ParcelItem> getAllEntities();

    ParcelItem getEntityById(long id);

    ParcelItem saveEntity(ParcelItem parcelItem);

    ParcelItem updateEntity(long id, ParcelItem parcelItem);

    boolean delete(long id);
}
