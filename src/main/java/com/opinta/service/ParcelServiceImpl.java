package com.opinta.service;

import com.opinta.dao.TariffGridDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.*;
import com.opinta.util.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {
    @Autowired
    private final TariffGridDao tariffGridDao;

    public ParcelServiceImpl(TariffGridDao tariffGridDao) {
        this.tariffGridDao = tariffGridDao;
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


    @Override
    public List<Parcel> getAllEntities() {
        return null;
    }

    @Override
    public Parcel getEntityById(long id) {
        return null;
    }

    @Override
    public Parcel saveEntity(Parcel parcel) {
        return null;
    }

    @Override
    public List<ParcelDto> getAll() {
        return null;
    }

    @Override
    public List<ParcelDto> getAllByClientId(long clientId) {
        return null;
    }

    @Override
    public ParcelDto getById(long id) {
        return null;
    }

    @Override
    public ParcelDto save(ParcelDto parcelDto) {
        return null;
    }

    @Override
    public ParcelDto update(long id, ParcelDto parcelDto) {
        return null;
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
