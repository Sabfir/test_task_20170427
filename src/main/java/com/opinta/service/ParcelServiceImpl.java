package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ShipmentDao;
import com.opinta.dao.TariffGridDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.*;
import com.opinta.mapper.ParcelMapper;
import com.opinta.util.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {
    private final ParcelDao parcelDao;
    private final TariffGridDao tariffGridDao;
    private final ShipmentDao shipmentDao;
    private final ParcelMapper parcelMapper;

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, TariffGridDao tariffGridDao, ShipmentDao shipmentDao,
                             ParcelMapper parcelMapper) {
        this.parcelDao = parcelDao;
        this.tariffGridDao = tariffGridDao;
        this.shipmentDao = shipmentDao;
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
        log.info("Getting Entity by id {}", id);
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
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ParcelDto save(ParcelDto parcelDto) {
        Parcel parcel = parcelMapper.toEntity(parcelDto);
        log.info("Saving parcel ", parcelDto);
        parcel.setPrice(calculatePrice(parcel));
        parcel.getParcelItems().forEach(parcelItem -> parcelItem.setParcel(parcel));
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
        target.setPrice(calculatePrice(target));
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for parcel", e);
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
            return false;
        }
        parcel.setId(id);
        log.info("Deleting parcel {}", parcel);
        parcelDao.delete(parcel);
        return true;
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
    public BigDecimal calculatePrice(Parcel parcel) {
        log.info("Calculating price for parcel {}", parcel);
        Shipment shipment = shipmentDao.getById(parcel.getShipment().getId());
        Address senderAddress = shipment.getSender().getAddress();
        Address recipientAddress = shipment.getRecipient().getAddress();
        W2wVariation w2wVariation = W2wVariation.COUNTRY;
        if (AddressUtil.isSameTown(senderAddress, recipientAddress)) {
            w2wVariation = W2wVariation.TOWN;
        } else if (AddressUtil.isSameRegion(senderAddress, recipientAddress)) {
            w2wVariation = W2wVariation.REGION;
        }
        TariffGrid tariffGrid = tariffGridDao.getLast(w2wVariation);
        if (parcel.getWeight() < tariffGrid.getWeight() &&
                parcel.getLength() < tariffGrid.getLength()) {
            tariffGrid = tariffGridDao.getByDimension(parcel.getWeight(), parcel.getLength(), w2wVariation);
        }
        log.info("TariffGrid for weight {} per length {} and type {}: {}",
                parcel.getWeight(), parcel.getLength(), w2wVariation, tariffGrid);
        if (tariffGrid == null) {
            return BigDecimal.ZERO;
        }
        float price = tariffGrid.getPrice() + getSurcharges(shipment);
        return new BigDecimal(Float.toString(price));
    }

    private float getSurcharges(Shipment shipment) {
        float surcharges = 0;
        if (shipment.getDeliveryType().equals(DeliveryType.D2W) ||
                shipment.getDeliveryType().equals(DeliveryType.W2D)) {
            surcharges += 9;
        } else if (shipment.getDeliveryType().equals(DeliveryType.D2D)) {
            surcharges += 12;
        }
        return surcharges;
    }
}
