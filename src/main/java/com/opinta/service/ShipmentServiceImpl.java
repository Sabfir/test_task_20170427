package com.opinta.service;

import com.opinta.dao.TariffGridDao;
import com.opinta.entity.Address;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.Parcel;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;
import com.opinta.util.AddressUtil;
import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import com.opinta.dao.ClientDao;
import com.opinta.dao.ShipmentDao;
import com.opinta.dto.ShipmentDto;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.Client;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import com.opinta.entity.Counterparty;
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

    @Autowired
    public ShipmentServiceImpl(ShipmentDao shipmentDao, ClientDao clientDao, TariffGridDao tariffGridDao,
                               ShipmentMapper shipmentMapper, BarcodeInnerNumberService barcodeInnerNumberService) {
        this.shipmentDao = shipmentDao;
        this.clientDao = clientDao;
        this.tariffGridDao = tariffGridDao;
        this.shipmentMapper = shipmentMapper;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
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
        log.info("Getting postcodePool by id {}", id);
        return shipmentDao.getById(id);
    }

    @Override
    @Transactional
    public Shipment saveEntity(Shipment shipment) {
        log.info("Saving shipment {}", shipment);
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
        calculatePrice(shipment);

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
        source.setSender(clientDao.getById(source.getSender().getId()));
		source.setRecipient(clientDao.getById(source.getRecipient().getId()));
		calculatePrice(source);
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for shipment", e);
        }
        target.setId(id);
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

    private void calculatePrice(Shipment shipment) {
        log.info("Calculating price for shipment {}", shipment);

        shipment.setPrice(BigDecimal.ZERO);
		if (shipment.getParcels() == null) {
			return;
		}

		W2wVariation w2wVariation = identifyW2wVariation(shipment);

		shipment.getParcels().forEach(parcel -> calculatePrice(w2wVariation, parcel));
		BigDecimal price = shipment.getParcels().stream().map(parcel -> parcel.getPrice()).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		shipment.setPrice(price.add(new BigDecimal(getSurcharges(shipment))));
    }

    private void calculatePrice(W2wVariation w2wVariation, Parcel parcel) {
		log.info("Calculating price for parcel {}", parcel);

		TariffGrid tariffGrid = tariffGridDao.getLast(w2wVariation);
		if (parcel.getWeight() < tariffGrid.getWeight() && parcel.getLength() < tariffGrid.getLength()) {
			tariffGrid = tariffGridDao.getByDimension(parcel.getWeight(), parcel.getLength(), w2wVariation);
		}

		log.info("TariffGrid for weight {} per length {} and type {}: {}", parcel.getWeight(), parcel.getLength(),
				w2wVariation, tariffGrid);

		if (tariffGrid == null) {
			parcel.setPrice(BigDecimal.ZERO);
			return;
		}

		parcel.setPrice(new BigDecimal(tariffGrid.getPrice()));
	}
    
    private W2wVariation identifyW2wVariation(Shipment shipment) {
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
}
