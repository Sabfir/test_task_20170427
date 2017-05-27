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
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

/**
 * Created by Dmytro Kushnir on 13.05.17.
 */
@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {

    private final ParcelDao parcelDao;
    private final ParcelMapper parcelMapper;
    private final ShipmentDao shipmentDao;
    private final TariffGridDao tariffGridDao;

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, ParcelMapper parcelMapper, ShipmentDao shipmentDao, TariffGridDao tariffGridDao) {
        this.parcelDao = parcelDao;
        this.parcelMapper = parcelMapper;
        this.shipmentDao = shipmentDao;
        this.tariffGridDao = tariffGridDao;

    }

    @Override
    @Transactional
    public List<Parcel> getAllEntities(long shipmentId) {
        return parcelDao.getAll(shipmentId);
    }

    @Override
    @Transactional
    public Parcel getEntityById(long id) {
        return parcelDao.getById(id);
    }

    @Override
    @Transactional
    public Parcel saveEntity(Parcel parcel) {
        return parcelDao.save(parcel);
    }

    @Override
    @Transactional
    public List<ParcelDto> getAll(long shipmentId) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        if(shipment == null){
            return null;
        }
        return parcelMapper.toDto(getAllEntities(shipmentId));
    }

    @Override
    @Transactional
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ParcelDto save(long shipmentId, ParcelDto parcelDto) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        if(shipment==null){
            return null;
        }
        Parcel parcel = parcelMapper.toEntity(parcelDto);
        Parcel saved = parcelDao.save(parcel);
        shipment.getParcels().add(saved);
        calculatePrices(shipment);
        shipment.setPrice(shipment.calculateTotalPrice());
        shipmentDao.update(shipment);
        return parcelMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ParcelDto update(long shipmentId, long id, ParcelDto parcelDto) {
        Parcel source = parcelMapper.toEntity(parcelDto);
        Parcel target = parcelDao.getById(id);
        if(target==null){
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for barcodeInnerNumber", e);
        }
        target.setId(id);
        parcelDao.update(target);
        Shipment shipment = shipmentDao.getById(shipmentId);
        calculatePrices(shipment);
        shipment.setPrice(shipment.calculateTotalPrice());
        shipmentDao.update(shipment);
        return parcelMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long shipmentId, long id) {
        Parcel parcel = parcelDao.getById(id);
        if(parcel==null) {
            return false;
        }
        parcelDao.delete(parcel);
        Shipment shipment = shipmentDao.getById(shipmentId);
        shipment.getParcels().remove(parcel);
        shipment.setPrice(shipment.calculateTotalPrice());
        shipmentDao.update(shipment);
        return true;
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

    @Override
    @Transactional
    public List<Parcel> calculatePrices(Shipment shipment){
        List<Parcel> parcels = shipment.getParcels();
        for (Parcel parcel: parcels) {
            log.info("Calculating price for shipment {}", shipment);

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
                return new ArrayList<>();
            }

            float price = tariffGrid.getPrice() + getSurcharges(shipment);
            parcel.setPrice(BigDecimal.valueOf(price));

        }

        return parcels;
    }

}
