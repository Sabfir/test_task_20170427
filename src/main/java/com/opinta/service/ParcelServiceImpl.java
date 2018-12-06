package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ShipmentDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
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
    private final ShipmentDao shipmentDao;

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, ParcelMapper parcelMapper, ShipmentDao shipmentDao) {
        this.parcelDao = parcelDao;
        this.parcelMapper = parcelMapper;
        this.shipmentDao = shipmentDao;
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
    @Transactional
    public List<ParcelDto> getAllByShipmentId(long shipmentId) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        if (shipment == null) {
            log.debug("Can't get parcelDto list by shipment. Shipment {} doesn't exist", shipmentId);
            return null;
        }
        log.info("Getting all parcels by shipment {}", shipmentId);
        return parcelMapper.toDto(parcelDao.getAllByShipmentId(shipmentId));
    }

    @Override
    @Transactional
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(getEntityById(id));
    }

    @Override
    public ParcelDto save(long shipmentId, ParcelDto parcelDto) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        if (shipment == null) {
            log.debug("Can't add parcelDto to shipment. Shipment {} doesn't exist", shipmentId);
            return null;
        }
        Parcel parcel = parcelMapper.toEntity(parcelDto);
        Parcel parcelSaved = parcelDao.save(parcel);
        shipment.getParcels().add(parcelSaved);
        log.info("Adding parcel {} to shipment {}", parcel, shipment);
        shipmentDao.update(shipment);
        return parcelMapper.toDto(parcelSaved);
    }

    @Override
    @Transactional
    public ParcelDto update(long id, ParcelDto parcelDto) {
        Parcel source = parcelMapper.toEntity(parcelDto);
        Parcel target = parcelDao.getById(id);
        if (target == null) {
            log.debug("Can't update parcel. Parcel doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Cant't get properties from object to updatable object for parcel", e);
        }
        target.setId(id);
        log.info("Updating parcel {}", target);
        parcelDao.update(target);
        return parcelMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Parcel parcel = parcelDao.getById(id);
        if (parcel == null) {
            log.debug("Can't delete parcel. Parcel doesn't exist {}", id);
        }
        parcel.setId(id);
        log.info("Deleting parcel {}", id);
        parcelDao.delete(parcel);
        return true;
    }
}
