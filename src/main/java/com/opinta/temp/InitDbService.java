package com.opinta.temp;

import com.opinta.dto.*;
import com.opinta.entity.*;
import com.opinta.mapper.*;
import com.opinta.service.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

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

    private ParcelService parcelService;
    private ParcelMapper parcelMapper;
    private ParcelItemService parcelItemService;
    private ParcelItemMapper parcelItemMapper;

    @Autowired
    public InitDbService(
            BarcodeInnerNumberService barcodeInnerNumberService, PostcodePoolService postcodePoolService,
            ClientService clientService, AddressService addressService, ShipmentService shipmentService,
            CounterpartyService counterpartyService, PostOfficeService postOfficeService,
            ShipmentTrackingDetailService shipmentTrackingDetailService, TariffGridService tariffGridService,
            ClientMapper clientMapper, AddressMapper addressMapper, PostcodePoolMapper postcodePoolMapper,
            BarcodeInnerNumberMapper barcodeInnerNumberMapper, ShipmentMapper shipmentMapper,
            PostOfficeMapper postOfficeMapper, CounterpartyMapper counterpartyMapper,
            ShipmentTrackingDetailMapper shipmentTrackingDetailMapper, ParcelService parcelService,
            ParcelMapper parcelMapper, ParcelItemService parcelItemService, ParcelItemMapper parcelItemMapper) {
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
        this.parcelService = parcelService;
        this.parcelMapper = parcelMapper;
        this.parcelItemService = parcelItemService;
        this.parcelItemMapper = parcelItemMapper;
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

        // create Shipment
        List<ShipmentDto> shipmentsSaved = new ArrayList<>();
        Shipment shipment1 = new Shipment(clientsSaved.get(0), clientsSaved.get(1), DeliveryType.W2W, new BigDecimal("2.5"), new BigDecimal("15"));
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment1)));

        Shipment shipment2 = new Shipment(clientsSaved.get(0), clientsSaved.get(0), DeliveryType.W2D, new BigDecimal("0.5"), new BigDecimal("20.5"));
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment2)));

        Shipment shipment3 = new Shipment(clientsSaved.get(1), clientsSaved.get(0), DeliveryType.D2D, new BigDecimal("2.25"), new BigDecimal("13.5"));
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment3)));

        // create Parcel
        List<ParcelDto> parcelsSaved = new ArrayList<>();
        Parcel parcel1 = new Parcel(1,1,1,1, new BigDecimal(110));
        parcel1.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(0)));
        parcelsSaved.add(parcelService.save(parcelMapper.toDto(parcel1)));

        Parcel parcel2 = new Parcel(2,2,2,2, new BigDecimal(40));
        parcel2.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(0)));
        parcelsSaved.add(parcelService.save(parcelMapper.toDto(parcel2)));

        Parcel parcel3 = new Parcel(3,3,3,3, new BigDecimal(3030));
        parcel3.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(1)));
        parcelsSaved.add(parcelService.save(parcelMapper.toDto(parcel3)));

        Parcel parcel4 = new Parcel(1,1,1,1, new BigDecimal(110));
        parcel4.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(1)));
        parcelsSaved.add(parcelService.save(parcelMapper.toDto(parcel4)));

        Parcel parcel5 = new Parcel(2,2,2,2, new BigDecimal(40));
        parcel5.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(2)));
        parcelsSaved.add(parcelService.save(parcelMapper.toDto(parcel5)));

        Parcel parcel6 = new Parcel(3,3,3,3, new BigDecimal(3030));
        parcel6.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(2)));
        parcelsSaved.add(parcelService.save(parcelMapper.toDto(parcel6)));

        // create ParcelItemLists
        List<ParcelItemDto> parcelItemSaved = new ArrayList<>();

        ParcelItem parcelItem1 = new ParcelItem("passport", 1, 0.1f, new BigDecimal("100"));
        parcelItem1.setParcel(parcelMapper.toEntity(parcelsSaved.get(0)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem1)));
        parcelItem1.setParcel(parcelMapper.toEntity(parcelsSaved.get(3)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem1)));

        ParcelItem parcelItem2 = new ParcelItem("proxy", 1, 0.1f, new BigDecimal("10"));
        parcelItem2.setParcel(parcelMapper.toEntity(parcelsSaved.get(0)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem2)));
        parcelItem2.setParcel(parcelMapper.toEntity(parcelsSaved.get(3)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem2)));

        ParcelItem parcelItem3 = new ParcelItem("computer mous", 2, 0.2f, new BigDecimal("5"));
        parcelItem3.setParcel(parcelMapper.toEntity(parcelsSaved.get(1)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem3)));
        parcelItem3.setParcel(parcelMapper.toEntity(parcelsSaved.get(4)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem3)));

        ParcelItem parcelItem4 = new ParcelItem("keyboard", 2, 0.2f, new BigDecimal("15"));
        parcelItem4.setParcel(parcelMapper.toEntity(parcelsSaved.get(1)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem4)));
        parcelItem4.setParcel(parcelMapper.toEntity(parcelsSaved.get(4)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem4)));

        ParcelItem parcelItem5 = new ParcelItem("bottle of water", 3, 1f, new BigDecimal("10"));
        parcelItem5.setParcel(parcelMapper.toEntity(parcelsSaved.get(2)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem5)));
        parcelItem5.setParcel(parcelMapper.toEntity(parcelsSaved.get(5)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem5)));

        ParcelItem parcelItem6 = new ParcelItem("bottle of wine", 3, 1f, new BigDecimal("1000"));
        parcelItem6.setParcel(parcelMapper.toEntity(parcelsSaved.get(2)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem6)));
        parcelItem6.setParcel(parcelMapper.toEntity(parcelsSaved.get(5)));
        parcelItemSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem6)));


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
