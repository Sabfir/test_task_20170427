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
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelItemServiceImpl implements ParcelItemService {

    private final ParcelItemDao parcelItemDao;
    private final ParcelItemMapper parcelItemMapper;
    private final ParcelDao parcelDao;

    @Autowired
    public ParcelItemServiceImpl(ParcelItemDao parcelItemDao, ParcelItemMapper parcelItemMapper, ParcelDao parcelDao) {
        this.parcelItemDao = parcelItemDao;
        this.parcelItemMapper = parcelItemMapper;
        this.parcelDao = parcelDao;
    }

    @Override
    @Transactional
    public ParcelItemDto save(ParcelItemDto parcelItemDto) {
        return parcelItemMapper.toDto(parcelItemDao.save(parcelItemMapper.toEntity(parcelItemDto)));
    }

    @Override
    @Transactional
    public ParcelItemDto update(long id, ParcelItemDto parcelItemDto) {
        ParcelItem target = parcelItemDao.getById(id);
        ParcelItem source = parcelItemMapper.toEntity(parcelItemDto);
        if (target == null) {
            log.debug("Cannot update parcel item. Parcel item {} does not exist.", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            log.error("Can't get properties from object to updatable object for parcel item", ex);
        }
        log.info("Updating parcel item {}", target);
        target.setId(id);
        parcelItemDao.update(target);
        return parcelItemMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        ParcelItem parcelItem = parcelItemDao.getById(id);
        if (parcelItem == null) {
            log.debug("Cannot delete parcel item. Parcel Item {} does not exist.", id);
            return false;
        }
        log.debug("Deleting parcel item {}", parcelItem);
        parcelItemDao.delete(parcelItem);
        return true;
    }

    @Override
    @Transactional
    public List<ParcelItem> getAllEntities() {
        return parcelItemDao.getAll();
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
    public List<ParcelItemDto> getAll() {
        return parcelItemMapper.toDto(parcelItemDao.getAll());
    }

    @Override
    @Transactional
    public List<ParcelItemDto> getAllByParcelId(long parcelId) {
        Parcel parcel = parcelDao.getById(parcelId);
        if (parcel == null) {
            log.debug("Cannot get parcel item list by parcel. Parcel {} does not exists.", parcelId);
            return null;
        }
        return parcelItemMapper.toDto(parcelItemDao.getAllByParcel(parcel));
    }

    @Override
    @Transactional
    public ParcelItemDto getById(long id) {
        return parcelItemMapper.toDto(parcelItemDao.getById(id));
    }
}
