package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.mapper.ParcelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {
    private final ParcelDao parcelDao;
    private final ParcelMapper parcelMapper;
    private final ParcelItemService parcelItemService

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, ParcelMapper parcelMapper, ParcelItemService parcelItemService) {
        this.parcelDao = parcelDao;
        this.parcelMapper = parcelMapper;
        this.parcelItemService = parcelItemService;
    }

    @Override
    @Transactional
    public List<Parcel> getAllEntities() {
        log.info("Getting all parcels");
        return parcelDao.getAll();
    }

    @Override
    @Transactional
    public Parcel getEntityById(long id) {
        log.info("Getting parcel by id {}", id);
        return parcelDao.getById(id);
    }

    @Override
    @Transactional
    public Parcel saveEntity(Parcel parcel) {
        log.info("Saving parcel {}", parcel);
        return parcelDao.save(parcel);
    }

    @Override
    public Parcel updateEntity(long id, Parcel source) {
        Parcel target = parcelDao.getById(id);
        if (target == null) {
            log.debug("Can't update parcel. Parcel doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for parcel", e);
        }
        target.setId(id);
        log.info("Updating parcel {}", target);
        parcelDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public List<ParcelDto> getAll() {
            return parcelMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ParcelDto save(ParcelDto parcelDto) {
        return parcelMapper.toDto(saveEntity(parcelMapper.toEntity(parcelDto)));
    }

    @Override
    @Transactional
    public ParcelDto update(long id, ParcelDto parcelDto) {
        Parcel parcel = updateEntity(id, parcelMapper.toEntity(parcelDto));
        return (parcel == null ? null : parcelMapper.toDto(parcel));
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Parcel parcel = parcelDao.getById(id);
        if (parcel == null) {
            log.debug("Can't delete parcel. ParcelItem doesn't exist {}", id);
            return false;
        }
        parcel.setId(id);
        log.info("Deleting parcel {}", parcel);
        parcelDao.delete(parcel);
        return true;
    }
}
