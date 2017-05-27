package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ParcelItemDao;
import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.mapper.ParcelItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

/**
 * Created by Dmytro Kushnir on 13.05.17.
 */
@Service
@Slf4j
public class ParcelItemServiceImpl implements ParcelItemService {

    private final ParcelItemDao parcelItemDao;
    private final ParcelDao parcelDao;
    private final ParcelItemMapper parcelItemMapper;

    @Autowired
    public ParcelItemServiceImpl(ParcelItemDao parcelItemDao, ParcelDao parcelDao, ParcelItemMapper parcelItemMapper) {
        this.parcelItemDao = parcelItemDao;
        this.parcelDao = parcelDao;
        this.parcelItemMapper = parcelItemMapper;
    }

    @Override
    @Transactional
    public List<ParcelItem> getAllEntities(long parcelId) {
        return parcelItemDao.getAll(parcelId);
    }

    @Override
    @Transactional
    public ParcelItem getEntityById(long id) {
        return parcelItemDao.getById(id);
    }

    @Override
    @Transactional
    public ParcelItem saveEntity(ParcelItem parcelItem) {
        return parcelItemDao.save(parcelItem);
    }

    @Override
    @Transactional
    public List<ParcelItemDto> getAll(long parcelId) {
        Parcel parcel = parcelDao.getById(parcelId);
        if(parcel == null){
            return null;
        }
        return parcelItemMapper.toDto(getAllEntities(parcelId));
    }

    @Override
    @Transactional
    public ParcelItemDto getById(long id) {
        return parcelItemMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ParcelItemDto save(long parcelId, ParcelItemDto parcelItemDto) {
        Parcel parcel = parcelDao.getById(parcelId);
        if (parcel==null){
            return null;
        }
        ParcelItem parcelItem = parcelItemMapper.toEntity(parcelItemDto);
        ParcelItem saved = parcelItemDao.save(parcelItem);
        parcel.getParcelItems().add(saved);
        parcelDao.save(parcel);
        return parcelItemMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ParcelItemDto update(long parcelId, long id, ParcelItemDto parcelItemDto) {
        ParcelItem source = parcelItemMapper.toEntity(parcelItemDto);
        ParcelItem target = parcelItemDao.getById(id);
        if(target==null){
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for barcodeInnerNumber", e);
        }
        target.setId(id);
        parcelItemDao.update(target);
        Parcel parcel = parcelDao.getById(parcelId);
        parcelDao.update(parcel);
        return parcelItemMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long parcelId, long id) {
        ParcelItem parcelItem = parcelItemDao.getById(id);
        if (parcelItem==null){
            return false;
        }
        parcelItemDao.delete(parcelItem);
        return true;
    }
}
