package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.TariffGridDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.*;
import com.opinta.mapper.ParcelMapper;
import com.opinta.util.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {

    private final TariffGridDao tariffGridDao;
    private final ParcelDao parcelDao;
    private final ParcelMapper parcelMapper;

    @Autowired
    public ParcelServiceImpl(TariffGridDao tariffGridDao, ParcelDao parcelDao, ParcelMapper parcelMapper) {
        this.tariffGridDao = tariffGridDao;
        this.parcelDao = parcelDao;
        this.parcelMapper = parcelMapper;
    }

    public BigDecimal calculatePrice(Parcel parcel, Shipment shipment) {
        log.info("Calculating price for parcel {}", parcel);

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

    @Transactional
    @Override
    public List<Parcel> getAllEntities() {
        log.info("Getting all parcels");
        return parcelDao.getAll();
    }
    @Transactional
    @Override
    public Parcel getEntityById(long id) {
        log.info("Getting parcel {}", id);
        return parcelDao.getById(id);
    }
    @Transactional
    @Override
    public Parcel saveEntity(Parcel parcel) {
        log.info("Saving parcels {}", parcel);
        return parcelDao.save(parcel);
    }
    @Transactional
    @Override
    public List<ParcelDto> getAll() {
        log.info("Getting all parcels");
        List<Parcel> parcels = parcelDao.getAll();
        return parcelMapper.toDto(parcels);
    }
    @Transactional
    @Override
    public ParcelDto getById(long id) {
        log.info("Getting parcel by id {}", id);
        Parcel parcel = parcelDao.getById(id);
        return parcelMapper.toDto(parcel);
    }
    @Transactional
    @Override
    public ParcelDto save(ParcelDto parcelDto) {
        log.info("Saving parcel {}", parcelDto);
        Parcel parcel = parcelMapper.toEntity(parcelDto);
        parcel = parcelDao.save(parcel);
        return parcelMapper.toDto(parcel);
    }
    @Transactional
    @Override
    public ParcelDto update(long id, ParcelDto parcelDto) {
        Parcel source = parcelMapper.toEntity(parcelDto);
        Parcel target = parcelDao.getById(id);
        if (target == null) {
            log.debug("Can't update parcel. Parcel doesn't exist {}", id);
            return null;
        }

        try {
            copyProperties(target, source);
        } catch (IllegalAccessException e) {
            log.error("Can't get properties from object to updatable object for parcel", e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for parcel", e);
            e.printStackTrace();
        }


        target.setId(id);
        log.info("Updating parcel {}", target);
        parcelDao.update(target);
        return parcelMapper.toDto(target);
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
