package com.opinta.service;

import com.opinta.dao.ParcelItemDao;
import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.ParcelItem;
import com.opinta.mapper.ParcelItemMapper;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelItemServiceImpl implements ParcelItemService {
    private final ParcelItemDao parcelItemDao;
    private final ParcelItemMapper parcelItemMapper;

    @Autowired
    public ParcelItemServiceImpl(ParcelItemDao parcelItemDao, ParcelItemMapper parcelItemMapper) {
        this.parcelItemDao = parcelItemDao;
        this.parcelItemMapper = parcelItemMapper;
    }


    @Override
    @Transactional
    public List<ParcelItem> getAllEntities() {
        log.info("Getting all parcel items");
        return parcelItemDao.getAll();
    }

    @Override
    @Transactional
    public ParcelItem getEntityById(long id) {
        log.info("Getting parcel item by id {}", id);
        return parcelItemDao.getById(id);
    }

    @Override
    @Transactional
    public ParcelItem saveEntity(ParcelItem parcelItem) {
        log.info("Saving parcel item {}", parcelItem);
        return parcelItemDao.save(parcelItem);
    }

    @Override
    @Transactional
    public List<ParcelItemDto> getAll() {
        return parcelItemMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public ParcelItemDto getById(long id) {
        return parcelItemMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ParcelItemDto save(ParcelItemDto parcelItemDto) {
        ParcelItem parcelItem = parcelItemDao.save(parcelItemMapper.toEntity(parcelItemDto));
        return parcelItemMapper.toDto(parcelItem);
    }

    @Override
    @Transactional
    public ParcelItemDto update(long id, ParcelItemDto parcelItemDto) {
        ParcelItem source = parcelItemMapper.toEntity(parcelItemDto);
        ParcelItem target = parcelItemDao.getById(id);
        if (target == null) {
            log.debug("Can't update parcelItem. ParcelItem doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for parcelItem", e);
        }
        target.setId(id);
        log.info("Updating parcelItem {}", target);
        parcelItemDao.update(target);
        return parcelItemMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        ParcelItem parcelItem = parcelItemDao.getById(id);
        if (parcelItem == null) {
            log.debug("Can't delete parcelItem. ParcelItem doesn't exist {}", id);
            return false;
        }
        parcelItem.setId(id);
        log.info("Deleting parcelItem {}", parcelItem);
        parcelItemDao.delete(parcelItem);
        return true;
    }
}
