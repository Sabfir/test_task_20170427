package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.TariffGridDao;
import com.opinta.entity.*;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import com.opinta.dao.ClientDao;
import com.opinta.dao.ShipmentDao;
import com.opinta.dto.ShipmentDto;
import com.opinta.mapper.ShipmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentDao shipmentDao;
    private final ClientDao clientDao;
    private final TariffGridDao tariffGridDao;
    private final ShipmentMapper shipmentMapper;
    private final BarcodeInnerNumberService barcodeInnerNumberService;
    private final ParcelService parcelService;
    private final ParcelDao parcelDao;

    @Autowired
    public ShipmentServiceImpl(ShipmentDao shipmentDao, ClientDao clientDao, TariffGridDao tariffGridDao,
                               ShipmentMapper shipmentMapper, BarcodeInnerNumberService barcodeInnerNumberService, ParcelService parcelService, ParcelDao parcelDao) {
        this.shipmentDao = shipmentDao;
        this.clientDao = clientDao;
        this.tariffGridDao = tariffGridDao;
        this.shipmentMapper = shipmentMapper;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
        this.parcelService = parcelService;
        this.parcelDao = parcelDao;
    }

    @Override
    @Transactional
    public List<Shipment> getAllEntities() {
        log.info("Getting all shipments");
        return shipmentDao.getAll();
    }

    @Override
    @Transactional
    public Shipment getEntityById(long id) {
        log.info("Getting shipment by id {}", id);
        return shipmentDao.getById(id);
    }

    @Override
    @Transactional
    public Shipment saveEntity(Shipment shipment) {
        log.info("Saving shipment {}", shipment);
        for (Parcel parcel1 : shipment.getParcels()) {
            parcel1.setShipment(shipment);
        }
        for (Parcel parcel : shipment.getParcels()) {
            for (ParcelItem parcelItem : parcel.getParcelItems()) {
                parcelItem.setParcel(parcel);
            }
        }
        return shipmentDao.save(shipment);
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAll() {
        return shipmentMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAllByClientId(long clientId) {
        Client client = clientDao.getById(clientId);
        if (client == null) {
            log.debug("Can't get shipment list by client. Client {} doesn't exist", clientId);
            return null;
        }
        log.info("Getting all shipments by client {}", client);
        return shipmentMapper.toDto(shipmentDao.getAllByClient(client));
    }

    @Override
    @Transactional
    public ShipmentDto getById(long id) {
        return shipmentMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ShipmentDto save(ShipmentDto shipmentDto) {
        Client existingClient = clientDao.getById(shipmentDto.getSenderId());
        Counterparty counterparty = existingClient.getCounterparty();
        PostcodePool postcodePool = counterparty.getPostcodePool();
        BarcodeInnerNumber newBarcode = barcodeInnerNumberService.generateBarcodeInnerNumber(postcodePool);
        postcodePool.getBarcodeInnerNumbers().add(newBarcode);
        Shipment shipment = shipmentMapper.toEntity(shipmentDto);
        shipment.setBarcode(newBarcode);
        log.info("Saving shipment with assigned barcode", shipmentMapper.toDto(shipment));

        shipment.setSender(clientDao.getById(shipment.getSender().getId()));
        shipment.setRecipient(clientDao.getById(shipment.getRecipient().getId()));

        shipment.setPrice(calculateAllPrice(shipment));
        shipment.getParcels().forEach(parcel -> parcel.setShipment(shipment));
        shipment.getParcels().forEach(parcel -> parcel.getParcelItems().forEach(parcelItem ->
                parcelItem.setParcel(parcel)));
        return shipmentMapper.toDto(shipmentDao.save(shipment));
    }

    @Override
    @Transactional
    public ShipmentDto update(long id, ShipmentDto shipmentDto) {
        Shipment source = shipmentMapper.toEntity(shipmentDto);
        Shipment target = shipmentDao.getById(id);
        if (target == null) {
            log.debug("Can't update shipment. Shipment doesn't exist {}", id);
            return null;
        }
        target.getParcels().forEach(parcel -> parcelService.delete(parcel.getId()));
        target.setPrice(calculateAllPrice(target));
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for shipment", e);
        }
        target.setId(id);
        target.setSender(clientDao.getById(target.getSender().getId()));
        target.setRecipient(clientDao.getById(target.getRecipient().getId()));
        target.getParcels().forEach(parcel -> parcel.setShipment(target));
        target.getParcels().forEach(parcel -> parcel.getParcelItems().forEach(parcelItem ->
                parcelItem.setParcel(parcel)));
        log.info("Updating shipment {}", target);
        shipmentDao.update(target);
        return shipmentMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Shipment shipment = shipmentDao.getById(id);
        if (shipment == null) {
            log.debug("Can't delete shipment. Shipment doesn't exist {}", id);
            return false;
        }
        shipment.setId(id);
        log.info("Deleting shipment {}", shipment);
        shipmentDao.delete(shipment);
        return true;
    }

    @Override
    public float getWeight(Shipment shipment) {
        float weight = 0f;
        List<Parcel> parcels = shipment.getParcels();
        for (Parcel parcel : parcels) {
            weight += parcel.getWeight();
        }
        return weight;
    }

        public BigDecimal calculateAllPrice(Shipment shipment){
            BigDecimal resultPrice = new BigDecimal(0);
            List<Parcel> parcels = shipment.getParcels();
            for (Parcel parcel : parcels) {
                resultPrice = resultPrice.add(parcel.getPrice());
            }
            return resultPrice;
        }

        @Override
        public BigDecimal getDeclaredPrice (Shipment shipment){
            BigDecimal declaredPrice = new BigDecimal(0);
            List<Parcel> parcels = shipment.getParcels();
            for (Parcel parcel : parcels) {
                declaredPrice = declaredPrice.add(parcel.getDeclaredPrice());
            }
            return declaredPrice;
        }
    }


