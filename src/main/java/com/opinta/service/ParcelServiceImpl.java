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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {
    private final ParcelDao parcelDao;
    private final TariffGridDao tariffGridDao;
    private final ParcelMapper parcelMapper;
    private final ShipmentService shipmentService;

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, TariffGridDao tariffGridDao, ParcelMapper parcelMapper,
                             ShipmentService shipmentService) {
        this.parcelDao = parcelDao;
        this.tariffGridDao = tariffGridDao;
        this.parcelMapper = parcelMapper;
        this.shipmentService = shipmentService;
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
        log.info("Getting parcel by id {}", id);
        return parcelDao.getById(id);
    }

    @Override
    @Transactional
    public Parcel saveEntity(Parcel parcel) {
        log.info("Saving parcelEntity {}", parcel);
        parcel.setPrice(calculatePrice(parcel));
        if (parcel.getShipment() != null) {
            Shipment shipment = shipmentService.getEntityById(parcel.getShipment().getId());
            BigDecimal currentPrice = shipment.getPrice();
            BigDecimal sumPrice = currentPrice.add(parcel.getPrice());
            shipment.setPrice(sumPrice);
            shipmentService.updateEntity(shipment);
            log.info("Update shipmentPrice from parcel saveEntity - {}", shipment.toString());
        }
        return parcelDao.save(parcel);
    }

    @Transactional
    @Override
    public List<ParcelDto> getAll() {
        return parcelMapper.toDto(getAllEntities());
    }

    @Transactional
    @Override
    public List<ParcelDto> getAllByShipmentId(long shipmentId) {
        List<ParcelDto> parcelDtoList = new ArrayList<>();
        shipmentService.getById(shipmentId).getParcelList().forEach(
                parcel -> parcelDtoList.add(parcelMapper.toDto(parcel)));
        return parcelDtoList;
    }

    @Transactional
    @Override
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(getEntityById(id));
    }

    @Transactional
    @Override
    public ParcelDto save(ParcelDto parcelDto) {
        Parcel parcel = parcelMapper.toEntity(parcelDto);
        parcel.setShipment(shipmentService.getEntityById(parcel.getShipment().getId()));
        log.info("Saving parcelDto {}", parcel);

        return parcelMapper.toDto(parcelDao.save(parcel));
    }

    @Transactional
    @Override
    public ParcelDto update(long id, ParcelDto parcelDto) {
        Parcel source = parcelMapper.toEntity(parcelDto);
        Parcel target = parcelDao.getById(id);

        if (target == null) {
            log.debug("Can't update parcel. Doesn't exist {}", id);
            return null;
        }
        source.setId(id);
        try {
            copyProperties(target, source);
        } catch (BeansException e) {
            log.error("Can't updateEntity, cos of parcel's properties absence", e);
        }
        target.setPrice(calculatePrice(target));
        log.info("Updating parcel {}", target);
        parcelDao.update(target);
        return parcelMapper.toDto(target);
    }

    @Transactional
    @Override
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

    private BigDecimal calculatePrice(Parcel parcel) {
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
        float price = getSurcharges(shipment);

        BigDecimal tariffPrice = BigDecimal.valueOf(tariffGrid.getPrice());
        BigDecimal shipmentPrice = shipment.getPrice();
        if (tariffPrice.compareTo(shipmentPrice) > 0) {
            price += tariffGrid.getPrice();
        } else {
            price += shipmentPrice.floatValue();
        }

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
