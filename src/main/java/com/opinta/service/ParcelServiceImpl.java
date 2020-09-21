package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.TariffGridDao;
import com.opinta.dto.ParcelDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Address;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;
import com.opinta.mapper.ParcelMapper;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.util.AddressUtil;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {
    private final ParcelDao parcelDao;
    private final ParcelMapper parcelMapper;
    private final TariffGridDao tariffGridDao;
    private final ShipmentMapper shipmentMapper;

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, ParcelMapper parcelMapper,
                             TariffGridDao tariffGridDao, ShipmentMapper shipmentMapper) {
        this.parcelDao = parcelDao;
        this.parcelMapper = parcelMapper;
        this.tariffGridDao = tariffGridDao;
        this.shipmentMapper = shipmentMapper;
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
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ParcelDto save(ParcelDto parcelDto) {
        Parcel parcel = parcelMapper.toEntity(parcelDto);
        return parcelMapper.toDto(parcelDao.save(parcel));
    }

    @Override
    @Transactional
    public List<ParcelDto> saveWithCalculatedPrice(ShipmentDto shipmentDto) {
        List<ParcelDto> parcels = new ArrayList<>();
        for (ParcelDto parcelDto : shipmentDto.getParcels()) {
            Parcel parcel = parcelMapper.toEntity(parcelDto);
            parcel.setPrice(calculatePrice(parcelDto, shipmentDto));
            parcels.add(parcelMapper.toDto(parcelDao.save(parcel)));
        }
        return parcels;
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
        } catch (IllegalAccessException | InvocationTargetException e) {
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

    private BigDecimal calculatePrice(ParcelDto parcelDto, ShipmentDto shipmentDto) {
        Shipment shipment = shipmentMapper.toEntity(shipmentDto);
        log.info("Calculating price for shipment {}", shipment);
        W2wVariation w2wVariation = getW2wVariation(shipment);
        TariffGrid tariffGrid = tariffGridDao.getLast(w2wVariation);
        if (isCheckedTariffGrid(tariffGrid, parcelDto)) {
            tariffGrid = tariffGridDao
                    .getByDimension(parcelDto.getWeight(), parcelDto.getLength(), w2wVariation);
        }
        log.info("TariffGrid for weight {} per length {} and type {}: {}",
                parcelDto.getWeight(), parcelDto.getLength(), w2wVariation, tariffGrid);
        if (tariffGrid == null) {
            return BigDecimal.ZERO;
        }
        float price = tariffGrid.getPrice() + getSurcharges(shipment);
        return new BigDecimal(Float.toString(price));
    }

    private W2wVariation getW2wVariation(Shipment shipment) {
        Address senderAddress = shipment.getSender().getAddress();
        Address recipientAddress = shipment.getRecipient().getAddress();
        W2wVariation w2wVariation = W2wVariation.COUNTRY;
        if (AddressUtil.isSameTown(senderAddress, recipientAddress)) {
            w2wVariation = W2wVariation.TOWN;
        } else if (AddressUtil.isSameRegion(senderAddress, recipientAddress)) {
            w2wVariation = W2wVariation.REGION;
        }
        return w2wVariation;
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

    private boolean isCheckedTariffGrid(TariffGrid tariffGrid, ParcelDto parcelDto) {
        return parcelDto.getWeight() < tariffGrid.getWeight()
                && parcelDto.getLength() < tariffGrid.getLength();
    }
}
