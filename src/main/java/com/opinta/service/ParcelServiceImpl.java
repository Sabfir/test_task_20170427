package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ShipmentDao;
import com.opinta.dao.TariffGridDao;
import com.opinta.entity.Parcel;
import com.opinta.entity.Address;
import com.opinta.entity.Shipment;
import com.opinta.entity.W2wVariation;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.DeliveryType;
import com.opinta.util.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {
    private final TariffGridDao tariffGridDao;
    private final ParcelDao parcelDao;
    private final ShipmentDao shipmentDao;

    public ParcelServiceImpl(TariffGridDao tariffGridDao, ParcelDao parcelDao, ShipmentDao shipmentDao) {
        this.tariffGridDao = tariffGridDao;
        this.parcelDao = parcelDao;
        this.shipmentDao = shipmentDao;
    }

    @Override
    public List<Parcel> getAll() {
        log.info("Getting all parcels");
        return parcelDao.getAll();
    }

    @Override
    @Transactional
    public Parcel getById(long id) {
        log.info("Getting parcel by id {}", id);
        return parcelDao.getById(id);
    }

    @Override
    @Transactional
    public Parcel save(Parcel parcel, long shipmentId) {
        parcel.setPrice(calculatePrice(parcel, shipmentDao.getById(shipmentId)));
        return parcelDao.save(parcel);
    }

    @Override
    @Transactional
    public Parcel update(long id, long shipmentId, Parcel sourceParcel) {
        Parcel target = parcelDao.getById(id);
        if (target == null) {
            log.debug("Can't update parcel. Parcel doesn't exist {}", id);
            return null;
        }

        try {
            copyProperties(target, sourceParcel);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for parcel", e);
        }

        target.setPrice(calculatePrice(target, shipmentDao.getById(shipmentId)));
        target.setId(id);
        log.info("Updating parcel {}", target);
        parcelDao.update(target);
        return target;
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
