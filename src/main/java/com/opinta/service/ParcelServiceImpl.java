package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ShipmentDao;
import com.opinta.dao.TariffGridDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import com.opinta.mapper.ParcelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {
    private final ParcelDao parcelDao;
    private final ShipmentDao shipmentDao;
    private final TariffGridDao tariffGridDao;
    private final ParcelMapper parcelMapper;

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, ShipmentDao shipmentDao, TariffGridDao tariffGridDao, ParcelMapper parcelMapper) {
        this.parcelDao = parcelDao;
        this.shipmentDao = shipmentDao;
        this.tariffGridDao = tariffGridDao;
        this.parcelMapper = parcelMapper;
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
    public List<ParcelDto> getAll() {
        return parcelMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public List<ParcelDto> getAllByShipmentId(long shipmentId) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        if (shipment == null) {
            log.debug("Can't get parcel list by shipment. Shipment {} doesn't exist", shipmentId);
            return null;
        }
        log.info("Getting all parcels by shipment {}", shipment);
        return parcelMapper.toDto(parcelDao.getAllByShipment(shipment));
    }

    @Override
    @Transactional
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ParcelDto save(ParcelDto parcelDto) {
        Parcel parcel = parcelMapper.toEntity(parcelDto);
        parcel.setPrice(calculatePrice(parcel));
        return parcelMapper.toDto(parcelDao.save(parcel));
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
        return null;
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Parcel parcel = parcelDao.getById(id);
        if (parcel == null) {
            log.debug("Can't delete parcel. Parcel doesn't exist {}", id);
            return false;
        }
        log.info("Deleting parcel {}", parcel);
        parcelDao.delete(parcel);
        return true;
    }

    private BigDecimal calculatePrice(Parcel parcel) {
        return new BigDecimal("33");
    }
}
