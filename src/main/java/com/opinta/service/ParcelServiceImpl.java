package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.TariffGridDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.Address;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;
import com.opinta.mapper.ParcelMapper;
import com.opinta.util.AddressUtil;
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
    private final TariffGridDao tariffGridDao;
    private final ParcelMapper parcelMapper;

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, TariffGridDao tariffGridDao, ParcelMapper parcelMapper) {
        this.parcelDao = parcelDao;
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

    public void fillFields(Shipment shipment, Parcel parcel) {
        parcel.setPrice(calculatePrice(shipment, parcel));
    }

    private BigDecimal calculatePrice(Shipment shipment, Parcel parcel) {
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
