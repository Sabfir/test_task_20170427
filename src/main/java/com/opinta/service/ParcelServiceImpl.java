package com.opinta.service;

import com.opinta.dao.ParcelDAO;
import com.opinta.dao.TariffGridDao;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import com.opinta.entity.Address;
import com.opinta.entity.W2wVariation;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.DeliveryType;
import com.opinta.util.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {
    private final ParcelDAO parcelDao;
    private final TariffGridDao tariffGridDao;

    @Autowired
    public ParcelServiceImpl(ParcelDAO parcelDao, TariffGridDao tariffGridDao) {
        this.parcelDao = parcelDao;
        this.tariffGridDao = tariffGridDao;
    }

    @Override
    public List<Parcel> getAll() {
        log.info("Getting all parcels");
        return parcelDao.getAll();
    }

    @Override
    public List<Parcel> getAllByShipment(Shipment shipment) {
        log.info("Getting parcels by shipment {}", shipment);
        return parcelDao.getAllByShipment(shipment);
    }

    @Override
    public BigDecimal calculatePrice(Parcel parcel) {
        log.info("Calculating price for parcel {}", parcel);

        Shipment shipment = parcel.getShipment();
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
