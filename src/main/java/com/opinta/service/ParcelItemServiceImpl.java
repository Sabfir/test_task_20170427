package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ParcelItemDao;
import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.mapper.ParcelItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelItemServiceImpl implements ParcelItemService{
    private final ParcelItemDao parcelItemDao;
    private final ParcelDao parcelDao;
    private final ParcelItemMapper parcelItemMapper;

    public ParcelItemServiceImpl(ParcelItemDao parcelItemDao, ParcelDao parcelDao, ParcelItemMapper parcelItemMapper) {
        this.parcelItemDao = parcelItemDao;
        this.parcelDao = parcelDao;
        this.parcelItemMapper = parcelItemMapper;
    }

    @Override
    @Transactional
    public List<ParcelItem> getAllEntitis() {
        log.info("Getting all parcelItems");
        return parcelItemDao.getAll();
    }

    @Override
    @Transactional
    public ParcelItem getEntityById(long id) {
        log.info("Getting parcelItem by id {}", id);
        return parcelItemDao.getById(id);
    }

    @Override
    @Transactional
    public ParcelItem saveEntity(ParcelItem parcelItem) {
        log.info("Saving parcelItem {}" , parcelItem);
        return parcelItemDao.save(parcelItem);
    }

    @Override
    @Transactional
    public List<ParcelItemDto> getAll() {
        return parcelItemMapper.toDto(getAllEntitis());
    }

    @Override
    @Transactional
    public List<ParcelItemDto> getAllByParcel(long parcelId) {
        Parcel parcel = parcelDao.getById(parcelId);
        if (parcel == null){
            log.debug("Can't get parcelItem list by parcel. Parcel {} doesn't exist", parcelId);
            return null;
        }
        log.info("Getting all parcelItems by parcel {}", parcel.getId());
        return parcelItemMapper.toDto(parcelItemDao.getAllByParcel(parcel));
    }

    @Override
    @Transactional
    public ParcelItemDto getById(long id) {
        return parcelItemMapper.toDto(parcelItemDao.getById(id));
    }

    @Override
    @Transactional
    public ParcelItemDto save(ParcelItemDto parcelItemDto) {
        log.info("Saving parcelItem ", parcelItemDto);
        return parcelItemMapper.toDto(saveEntity(parcelItemMapper.toEntity(parcelItemDto)));
    }

    @Override
    @Transactional
    public ParcelItemDto update(long id, ParcelItemDto parcelItemDto) {
        ParcelItem source = parcelItemMapper.toEntity(parcelItemDto);
        ParcelItem target = parcelItemDao.getById(id);
        if (target == null){
            log.debug("Can't update parcelItem. ParcelItem doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
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
        if (parcelItem == null){
            log.debug("Can't delete parcelItem. ParcelItem doesn't exist {}", id);
            return false;
        }
        parcelItem.setId(id);
        log.info("Deleting parcelItem {}", parcelItem);
        parcelItemDao.delete(parcelItem);
        return true;
    }
}
