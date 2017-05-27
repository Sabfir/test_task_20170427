package com.opinta.service;

import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.ParcelItem;

import java.util.List;

/**
 * Created by Dmytro Kushnir on 13.05.17.
 */
public interface ParcelItemService {

    List<ParcelItem> getAllEntities(long parcelId);

    ParcelItem getEntityById(long id);

    ParcelItem saveEntity(ParcelItem parcelItem);

    List<ParcelItemDto> getAll(long parcelId);

    ParcelItemDto getById(long id);

    ParcelItemDto save(long parcelId, ParcelItemDto parcelItemDto);

    ParcelItemDto update(long parcelId, long id, ParcelItemDto parcelItemDto);

    boolean delete(long parcelId, long id);

}
