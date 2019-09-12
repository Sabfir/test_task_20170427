package com.opinta.service;

import com.opinta.dao.ParcelItemDao;
import com.opinta.dto.ParcelItemDto;
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
@Transactional
public class ParcelItemServiceImpl implements ParcelItemService {

    private final ParcelItemDao parcelItemDao;
    private final ParcelItemMapper parcelItemMapper;

    @Autowired
    public ParcelItemServiceImpl(ParcelItemDao parcelItemDao, ParcelItemMapper parcelItemMapper) {
        this.parcelItemDao = parcelItemDao;
        this.parcelItemMapper = parcelItemMapper;
    }

    @Override
    public List<ParcelItem> getAllEntities() {
        log.info("Getting all parcelItems");
        return parcelItemDao.getAll();
    }

    @Override
    public ParcelItem getEntityById(long id) {
        log.info("Getting parcelItem by id {}", id);
        return parcelItemDao.getById(id);
    }

    @Override
    public ParcelItem saveEntity(ParcelItem parcelItem) {
        log.info("Saving parcelItem {}", parcelItem);
        return parcelItemDao.save(parcelItem);
    }

    @Override
    public ParcelItem updateEntity(long id, ParcelItem source) {
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
        return target;
    }

    @Override
    public List<ParcelItemDto> getAll() {
        return parcelItemMapper.toDto(getAllEntities());
    }

    @Override
    public ParcelItemDto getById(long id) {
        return parcelItemMapper.toDto(getEntityById(id));
    }

    @Override
    public ParcelItemDto save(ParcelItemDto parcelItemDto) {
        return parcelItemMapper.toDto(saveEntity(parcelItemMapper.toEntity(parcelItemDto)));
    }

    @Override
    public ParcelItemDto update(long id, ParcelItemDto parcelItemDto) {
        ParcelItem parcelItem = updateEntity(id, parcelItemMapper.toEntity(parcelItemDto));
        return parcelItem == null ? null : parcelItemMapper.toDto(parcelItem);
    }

    @Override
    public boolean delete(long id) {
        ParcelItem parcelItem = parcelItemDao.getById(id);
        if (parcelItem == null) {
            log.debug("Can't delete parcelItem. ParcelItem doesn't exist {}", id);
            return false;
        }
        log.info("Deleting parcelItem {}", parcelItem);
        parcelItemDao.delete(parcelItem);
        return true;
    }
}
