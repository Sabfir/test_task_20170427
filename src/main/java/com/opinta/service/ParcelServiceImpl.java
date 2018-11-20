package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ShipmentDao;
import com.opinta.dao.TariffGridDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;
import com.opinta.mapper.ParcelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.partitioningBy;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {

    private final ParcelDao parcelDao;
    private final ParcelMapper parcelMapper;
    private final ShipmentDao shipmentDao;
    private final TariffGridDao tariffGridDao;

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, ParcelMapper parcelMapper,
                             ShipmentDao shipmentDao, TariffGridDao tariffGridDao) {
        this.parcelDao = parcelDao;
        this.parcelMapper = parcelMapper;
        this.shipmentDao = shipmentDao;
        this.tariffGridDao = tariffGridDao;
    }

    @Override
    @Transactional
    public List<Parcel> getAllEntities() {
        return parcelDao.getAll();
    }

    @Override
    @Transactional
    public Parcel getEntityById(long id) {
        return parcelDao.getById(id);
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
            log.debug("Cannot get parcel list by shipment. Shipment {} does not exists.", shipmentId);
            return null;
        }
        return parcelMapper.toDto(parcelDao.getAllByShipment(shipment));
    }

    @Override
    @Transactional
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(parcelDao.getById(id));
    }

    @Override
    @Transactional
    public ParcelDto save(ParcelDto parcelDto) {
        return parcelMapper.toDto(parcelDao.save(parcelMapper.toEntity(parcelDto)));
    }

    @Override
    @Transactional
    public ParcelDto update(long id, ParcelDto parcelDto) {
        Parcel source = parcelMapper.toEntity(parcelDto);
        Parcel target = parcelDao.getById(id);
        if (target == null) {
            log.debug("Cannot update parcel {}. Parcel does not exist.", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            log.error("Can't get properties from object to updatable object for shipment", ex);
        }
        target.setId(id);
        parcelDao.update(target);
        return parcelMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Parcel parcel = parcelDao.getById(id);
        if (parcel == null) {
            log.debug("Cannot delete parcel {}. Parcel does not exist.", id);
            return false;
        }
        log.debug("Deleting parcel {}.", parcel);
        parcelDao.delete(parcel);
        return true;
    }

    @Override
    @Transactional
    public Parcel saveEntity(Parcel parcel) {
        return parcelDao.save(parcel);
    }

    @Override
    public BigDecimal calculateParcelsPrice(List<Parcel> parcels, W2wVariation w2wVariation) {
        BigDecimal totalPrice = new BigDecimal(0);
        TariffGrid biggestTarrif = tariffGridDao.getLast(w2wVariation);
        Map<Boolean, List<Parcel>> partitioned = parcels.stream()
                .collect(partitioningBy(p -> p.getWeight() <= biggestTarrif.getWeight()));
        List<Parcel> calculatableParcels = partitioned.get(true);
        for (Parcel parcel : calculatableParcels) {
            totalPrice = totalPrice.add(calculateParcelPrice(parcel, biggestTarrif, w2wVariation));
        }
        return totalPrice;
    }

    private BigDecimal calculateParcelPrice(Parcel parcel, TariffGrid tariffGrid, W2wVariation w2wVariation) {
        if (parcel.getWeight() < tariffGrid.getWeight() &&
                parcel.getLength() < tariffGrid.getLength()) {
            tariffGrid = tariffGridDao.getByDimension(parcel.getWeight(), parcel.getLength(), w2wVariation);
        }

        log.info("TariffGrid for weight {} per length {} and type {}: {}",
                parcel.getWeight(), parcel.getLength(), w2wVariation, tariffGrid);

        if (tariffGrid == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(tariffGrid.getPrice());
    }
}
