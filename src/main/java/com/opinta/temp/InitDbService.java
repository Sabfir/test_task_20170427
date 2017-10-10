package com.opinta.temp;

import com.opinta.dto.*;
import com.opinta.entity.*;
import com.opinta.mapper.*;
import com.opinta.service.ShipmentTrackingDetailService;
import com.opinta.service.TariffGridService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import com.opinta.service.AddressService;
import com.opinta.service.BarcodeInnerNumberService;
import com.opinta.service.ClientService;
import com.opinta.service.PostOfficeService;
import com.opinta.service.PostcodePoolService;
import com.opinta.service.ShipmentService;
import com.opinta.service.CounterpartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.opinta.entity.BarcodeStatus.RESERVED;
import static com.opinta.entity.BarcodeStatus.USED;

@Service
public class InitDbService {
    private BarcodeInnerNumberService barcodeInnerNumberService;
    private PostcodePoolService postcodePoolService;
    private ClientService clientService;
    private AddressService addressService;
    private ShipmentService shipmentService;
    private CounterpartyService counterpartyService;
    private PostOfficeService postOfficeService;
    private ShipmentTrackingDetailService shipmentTrackingDetailService;
    private TariffGridService tariffGridService;

    private ClientMapper clientMapper;
    private AddressMapper addressMapper;
    private PostcodePoolMapper postcodePoolMapper;
    private BarcodeInnerNumberMapper barcodeInnerNumberMapper;
    private ShipmentMapper shipmentMapper;
    private PostOfficeMapper postOfficeMapper;
    private CounterpartyMapper counterpartyMapper;
    private ShipmentTrackingDetailMapper shipmentTrackingDetailMapper;
    private ParcelMapper parcelMapper;

    @Autowired
    public InitDbService(
            BarcodeInnerNumberService barcodeInnerNumberService, PostcodePoolService postcodePoolService,
            ClientService clientService, AddressService addressService, ShipmentService shipmentService,
            CounterpartyService counterpartyService, PostOfficeService postOfficeService,
            ShipmentTrackingDetailService shipmentTrackingDetailService, TariffGridService tariffGridService,
            ClientMapper clientMapper, AddressMapper addressMapper, PostcodePoolMapper postcodePoolMapper,
            BarcodeInnerNumberMapper barcodeInnerNumberMapper, ShipmentMapper shipmentMapper,
            PostOfficeMapper postOfficeMapper, CounterpartyMapper counterpartyMapper,
            ShipmentTrackingDetailMapper shipmentTrackingDetailMapper, ParcelMapper parcelMapper) {
        this.barcodeInnerNumberService = barcodeInnerNumberService;
        this.postcodePoolService = postcodePoolService;
        this.clientService = clientService;
        this.addressService = addressService;
        this.shipmentService = shipmentService;
        this.counterpartyService = counterpartyService;
        this.postOfficeService = postOfficeService;
        this.shipmentTrackingDetailService = shipmentTrackingDetailService;
        this.tariffGridService = tariffGridService;
        this.clientMapper = clientMapper;
        this.addressMapper = addressMapper;
        this.postcodePoolMapper = postcodePoolMapper;
        this.barcodeInnerNumberMapper = barcodeInnerNumberMapper;
        this.shipmentMapper = shipmentMapper;
        this.postOfficeMapper = postOfficeMapper;
        this.counterpartyMapper = counterpartyMapper;
        this.shipmentTrackingDetailMapper = shipmentTrackingDetailMapper;
        this.parcelMapper = parcelMapper;
    }

    @PostConstruct
    public void init() {
        populateDb();
    }

    private void populateDb() {
        // populate TariffGrid
        populateTariffGrid();

        // create PostcodePool with BarcodeInnerNumber
        PostcodePoolDto postcodePoolDto = postcodePoolMapper.toDto(new PostcodePool("00001", false));
        final long postcodePoolId = postcodePoolService.save(postcodePoolDto).getId();

        List<BarcodeInnerNumberDto> barcodeInnerNumbers = new ArrayList<>();
        barcodeInnerNumbers.add(barcodeInnerNumberMapper.toDto(new BarcodeInnerNumber("0000001", USED)));
        barcodeInnerNumbers.add(barcodeInnerNumberMapper.toDto(new BarcodeInnerNumber("0000002", RESERVED)));
        barcodeInnerNumbers.add(barcodeInnerNumberMapper.toDto(new BarcodeInnerNumber("0000003", RESERVED)));

        postcodePoolService.addBarcodeInnerNumbers(postcodePoolId, barcodeInnerNumbers);

        // create Address
        List<AddressDto> addresses = new ArrayList<>();
        List<AddressDto> addressesSaved = new ArrayList<>();
        addresses.add(addressMapper.toDto(new Address("00001", "Ternopil", "Monastiriska", "Monastiriska", "Sadova", "51", "")));
        addresses.add(addressMapper.toDto(new Address("00002", "Kiev", "", "Kiev", "Khreschatik", "121", "37")));
        addresses.forEach((AddressDto addressDto) -> addressesSaved.add(addressService.save(addressDto)));

        // create Client with Counterparty
        PostcodePoolDto postcodePoolDto1 = postcodePoolMapper.toDto(new PostcodePool("00003", false));
        PostcodePoolDto postcodePoolDtoSaved1 = postcodePoolService.save(postcodePoolDto1);
        Counterparty counterparty = new Counterparty("Modna kasta",
                postcodePoolMapper.toEntity(postcodePoolDtoSaved1));
        CounterpartyDto counterpartyDto = this.counterpartyMapper.toDto(counterparty);
        counterpartyDto = counterpartyService.save(counterpartyDto);
        counterparty = counterpartyMapper.toEntity(counterpartyDto);
        List<Client> clients = new ArrayList<>();
        List<Client> clientsSaved = new ArrayList<>();
        clients.add(new Client("FOP Ivanov", "001",
                addressMapper.toEntity(addressesSaved.get(0)), counterparty));
        clients.add(new Client("Petrov PP", "002",
                addressMapper.toEntity(addressesSaved.get(1)), counterparty));
        clients.forEach((Client client) ->
            clientsSaved.add(this.clientMapper.toEntity(clientService.save(this.clientMapper.toDto(client))))
        );

        // create Shipments with Parcels
        List<ShipmentDto> shipmentsSaved = new ArrayList<>();
        Shipment shipment1 = new Shipment(clientsSaved.get(0), clientsSaved.get(1), DeliveryType.W2W,
                new BigDecimal("2.5"), new BigDecimal("15"));
        shipment1 = shipmentService.saveEntity(shipment1);
        List<ParcelDto> parcelDtos1 = new ArrayList<>();
        parcelDtos1.add(parcelMapper.toDto(new Parcel(shipment1, 1, 1, new BigDecimal("12.5"), new BigDecimal("2.5"))));
        parcelDtos1.add(parcelMapper.toDto(new Parcel(shipment1, 1, 2, new BigDecimal("10"), new BigDecimal("3"))));
        parcelDtos1.add(parcelMapper.toDto(new Parcel(shipment1, 1, 3, new BigDecimal("5"), new BigDecimal("10"))));
        shipmentService.addParcels(shipment1.getId(), parcelDtos1);
        shipmentsSaved.add(shipmentMapper.toDto(shipment1));
        Shipment shipment2 = new Shipment(clientsSaved.get(0), clientsSaved.get(0), DeliveryType.W2D,
                new BigDecimal("0.5"), new BigDecimal("20.5"));
        shipment2 = shipmentService.saveEntity(shipment2);
        List<ParcelDto> parcelDtos2 = new ArrayList<>();
        parcelDtos2.add(parcelMapper.toDto(new Parcel(shipment2, 2, 1, new BigDecimal("15"), new BigDecimal("8"))));
        parcelDtos2.add(parcelMapper.toDto(new Parcel(shipment2, 2, 2, new BigDecimal("8"), new BigDecimal("6"))));
        parcelDtos2.add(parcelMapper.toDto(new Parcel(shipment2, 2, 3, new BigDecimal("12"), new BigDecimal("3"))));
        shipmentService.addParcels(shipment2.getId(), parcelDtos2);
        shipmentsSaved.add(shipmentMapper.toDto(shipment2));
        Shipment shipment3 = new Shipment(clientsSaved.get(1), clientsSaved.get(0), DeliveryType.D2D,
                new BigDecimal("2.25"), new BigDecimal("13.5"));
        shipment3 = shipmentService.saveEntity(shipment3);
        List<ParcelDto> parcels3 = new ArrayList<>();
        parcels3.add(parcelMapper.toDto(new Parcel(shipment3, 3, 1, new BigDecimal("20"), new BigDecimal("1"))));
        parcels3.add(parcelMapper.toDto(new Parcel(shipment3, 3, 2, new BigDecimal("13"), new BigDecimal("21"))));
        parcels3.add(parcelMapper.toDto(new Parcel(shipment3, 3, 3, new BigDecimal("4"), new BigDecimal("20"))));
        shipmentService.addParcels(shipment3.getId(), parcels3);
        shipmentsSaved.add(shipmentMapper.toDto(shipment3));

        // create PostOffice
        PostcodePoolDto postcodePoolDto2 = postcodePoolMapper.toDto(new PostcodePool("00002", false));
        PostcodePoolDto postcodePoolDtoSaved = postcodePoolService.save(postcodePoolDto2);
        PostOffice postOffice = new PostOffice("Lviv post office", addressMapper.toEntity(addressesSaved.get(0)),
                postcodePoolMapper.toEntity(postcodePoolDtoSaved));
        PostOfficeDto postOfficeSaved = postOfficeService.save(postOfficeMapper.toDto(postOffice));

        // create ShipmentTrackingDetail
        ShipmentTrackingDetail shipmentTrackingDetail =
                new ShipmentTrackingDetail(shipmentMapper.toEntity(shipmentsSaved.get(0)),
                        postOfficeMapper.toEntity(postOfficeSaved), ShipmentStatus.PREPARED, new Date());
        shipmentTrackingDetailService.save(shipmentTrackingDetailMapper.toDto(shipmentTrackingDetail));
    }

    private void populateTariffGrid() {
        List<TariffGrid> tariffGrids = new ArrayList<>();

        tariffGrids.add(new TariffGrid(0.25f, 30f, W2wVariation.TOWN, 12f));
        tariffGrids.add(new TariffGrid(0.25f, 30f, W2wVariation.REGION, 15f));
        tariffGrids.add(new TariffGrid(0.25f, 30f, W2wVariation.COUNTRY, 21f));

        tariffGrids.add(new TariffGrid(0.5f, 30f, W2wVariation.TOWN, 15f));
        tariffGrids.add(new TariffGrid(0.5f, 30f, W2wVariation.REGION, 18f));
        tariffGrids.add(new TariffGrid(0.5f, 30f, W2wVariation.COUNTRY, 24f));

        tariffGrids.add(new TariffGrid(1f, 30f, W2wVariation.TOWN, 18f));
        tariffGrids.add(new TariffGrid(1f, 30f, W2wVariation.REGION, 21f));
        tariffGrids.add(new TariffGrid(1f, 30f, W2wVariation.COUNTRY, 27f));

        tariffGrids.add(new TariffGrid(2f, 30f, W2wVariation.TOWN, 21f));
        tariffGrids.add(new TariffGrid(2f, 30f, W2wVariation.REGION, 24f));
        tariffGrids.add(new TariffGrid(2f, 30f, W2wVariation.COUNTRY, 30f));

        tariffGrids.add(new TariffGrid(5f, 70f, W2wVariation.TOWN, 24f));
        tariffGrids.add(new TariffGrid(5f, 70f, W2wVariation.REGION, 27f));
        tariffGrids.add(new TariffGrid(5f, 70f, W2wVariation.COUNTRY, 36f));

        tariffGrids.add(new TariffGrid(10f, 70f, W2wVariation.TOWN, 27f));
        tariffGrids.add(new TariffGrid(10f, 70f, W2wVariation.REGION, 30f));
        tariffGrids.add(new TariffGrid(10f, 70f, W2wVariation.COUNTRY, 42f));

        tariffGrids.add(new TariffGrid(15f, 70f, W2wVariation.TOWN, 30f));
        tariffGrids.add(new TariffGrid(15f, 70f, W2wVariation.REGION, 36f));
        tariffGrids.add(new TariffGrid(15f, 70f, W2wVariation.COUNTRY, 48f));

        tariffGrids.add(new TariffGrid(20f, 70f, W2wVariation.TOWN, 36f));
        tariffGrids.add(new TariffGrid(20f, 70f, W2wVariation.REGION, 42f));
        tariffGrids.add(new TariffGrid(20f, 70f, W2wVariation.COUNTRY, 54f));

        tariffGrids.add(new TariffGrid(30f, 70f, W2wVariation.TOWN, 42f));
        tariffGrids.add(new TariffGrid(30f, 70f, W2wVariation.REGION, 48f));
        tariffGrids.add(new TariffGrid(30f, 70f, W2wVariation.COUNTRY, 60f));

        tariffGrids.forEach(tariffGridService::save);
    }
}
