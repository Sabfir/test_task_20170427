package com.opinta.service;

import com.opinta.dao.ParcelItemDAO;
import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.Shipment;
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
    private final ParcelItemDAO parcelItemDAO;
    private final ParcelItemMapper parcelItemMapper;

    @Autowired
    public ParcelItemServiceImpl(ParcelItemDAO parcelItemDAO, ParcelItemMapper parcelItemMapper) {
        this.parcelItemDAO = parcelItemDAO;
        this.parcelItemMapper = parcelItemMapper;
    }

    @Override
    @Transactional
    public ParcelItem getEntityById(long id) {
        log.info("Getting parcel item by id {}", id);
        return parcelItemDAO.getById(id);
    }

    @Override
    @Transactional
    public List<ParcelItemDto> getAllByParcel(Parcel parcel) {
        log.info("Getting parcel items by parcel {}", parcel);
        return parcelItemMapper.toDto(parcelItemDAO.getAllByParcel(parcel));
    }

    @Override
    @Transactional
    public ParcelItemDto getById(long id) {
        log.info("Getting parcel item by id {}", id);
        return parcelItemMapper.toDto(parcelItemDAO.getById(id));
    }

    @Override
    @Transactional
    public ParcelItemDto save(ParcelItemDto parcelItemDto) {
        ParcelItem parcelItem = parcelItemMapper.toEntity(parcelItemDto);
        log.info("Saving new parcel item {}", parcelItem);
        return parcelItemMapper.toDto(parcelItemDAO.save(parcelItem));
    }

    @Override
    @Transactional
    public ParcelItemDto update(long id, ParcelItemDto parcelItemDto) {
        ParcelItem source = parcelItemMapper.toEntity(parcelItemDto);
        ParcelItem target = getEntityById(id);
        if (target == null) {
            log.debug("Can't update parcel item. Item doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for shipment", e);
        }
        log.info("Updating parcel item {}", target);
        parcelItemDAO.update(target);
        return parcelItemMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        ParcelItem item = parcelItemDAO.getById(id);
        if (item == null) {
            log.debug("Can't delete parcel item. Item doesn't exist {}", id);
            return false;
        }
        item.setParcel(null);
        log.info("Deleting parcel item {}", item);
        parcelItemDAO.delete(item);
        return true;
    }
}
