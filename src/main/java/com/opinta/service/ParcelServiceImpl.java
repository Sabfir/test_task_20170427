package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ParcelItemDao;
import com.opinta.dto.ParcelDto;
import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.mapper.ParcelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
@Transactional
public class ParcelServiceImpl implements ParcelService {

    private final ParcelDao parcelDao;
    private final ParcelMapper parcelMapper;
    private final ParcelItemDao parcelItemDao;

    private final ParcelItemService parcelItemService;

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, ParcelMapper parcelMapper, ParcelItemService parcelItemService,
                             ParcelItemDao parcelItemDao) {
        this.parcelDao = parcelDao;
        this.parcelMapper = parcelMapper;
        this.parcelItemService = parcelItemService;
        this.parcelItemDao = parcelItemDao;
    }

    @Override
    public List<Parcel> getAllEntities() {
        log.info("Getting all parcels");
        return parcelDao.getAll();
    }

    @Override
    public Parcel getEntityById(long id) {
        log.info("Getting parcel by id {}", id);
        return parcelDao.getById(id);
    }

    @Override
    public Parcel saveEntity(Parcel parcel) {
        log.info("Saving parcel {}", parcel);

        List<ParcelItem> savedParcelItems = new ArrayList<>();
        for (ParcelItem parcelItem : parcel.getParcelItems()) {
            savedParcelItems.add(parcelItemDao.save(parcelItem));
        }
        parcel.setParcelItems(savedParcelItems);

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
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for parcel", e);
        }
        target.setId(id);
        log.info("Updating parcel {}", target);
        parcelDao.update(target);
        return target;
    }

    @Override
    public List<ParcelDto> getAll() {
        return parcelMapper.toDto(getAllEntities());
    }

    @Override
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(getEntityById(id));
    }

    @Override
    public ParcelDto save(ParcelDto parcelDto) {
        List<ParcelItemDto> parcelItems = parcelDto.getParcelItems();
        List<ParcelItemDto> savedParcelItems = new ArrayList<>();
        for (ParcelItemDto parcelItem : parcelItems) {
            savedParcelItems.add(parcelItemService.save(parcelItem));
        }
        parcelDto.setParcelItems(savedParcelItems);

        Parcel parcel = parcelMapper.toEntity(parcelDto);
        return parcelMapper.toDto(saveEntity(parcel));
    }

    @Override
    public ParcelDto update(long id, ParcelDto parcelDto) {
        Parcel parcel = updateEntity(id, parcelMapper.toEntity(parcelDto));
        return parcel == null ? null : parcelMapper.toDto(parcel);
    }

    @Override
    public boolean delete(long id) {
        Parcel parcel = parcelDao.getById(id);
        if (parcel == null) {
            log.debug("Can't delete parcel. Parcel doesn't exist {}", id);
            return false;
        }
//        for (ParcelItem parcelItem : parcel.getParcelItems()) {
//            parcelItemService.delete(parcelItem.getId());
//        }
        log.info("Deleting parcel {}", parcel);
        parcelDao.delete(parcel);
        return true;
    }
}
