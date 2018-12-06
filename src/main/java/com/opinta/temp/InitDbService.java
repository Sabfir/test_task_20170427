package com.opinta.temp;

import com.opinta.dto.*;
import com.opinta.entity.*;
import com.opinta.mapper.ShipmentTrackingDetailMapper;
import com.opinta.service.ShipmentTrackingDetailService;
import com.opinta.service.TariffGridService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import com.opinta.mapper.AddressMapper;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import com.opinta.mapper.ClientMapper;
import com.opinta.mapper.PostOfficeMapper;
import com.opinta.mapper.PostcodePoolMapper;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.mapper.CounterpartyMapper;
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
    private final PostcodePoolService postcodePoolService;
    private final ClientService clientService;
    private final AddressService addressService;
    private final ShipmentService shipmentService;
    private final CounterpartyService counterpartyService;
    private final PostOfficeService postOfficeService;
    private final ShipmentTrackingDetailService shipmentTrackingDetailService;
    private final TariffGridService tariffGridService;

    private final ClientMapper clientMapper;
    private final AddressMapper addressMapper;
    private final PostcodePoolMapper postcodePoolMapper;
    private final BarcodeInnerNumberMapper barcodeInnerNumberMapper;
    private final ShipmentMapper shipmentMapper;
    private final PostOfficeMapper postOfficeMapper;
    private final CounterpartyMapper counterpartyMapper;
    private final ShipmentTrackingDetailMapper shipmentTrackingDetailMapper;

    @Autowired
    public InitDbService(
            BarcodeInnerNumberService barcodeInnerNumberService, PostcodePoolService postcodePoolService,
            ClientService clientService, AddressService addressService, ShipmentService shipmentService,
            CounterpartyService counterpartyService, PostOfficeService postOfficeService,
            ShipmentTrackingDetailService shipmentTrackingDetailService, TariffGridService tariffGridService,
            ClientMapper clientMapper, AddressMapper addressMapper, PostcodePoolMapper postcodePoolMapper,
            BarcodeInnerNumberMapper barcodeInnerNumberMapper, ShipmentMapper shipmentMapper,
            PostOfficeMapper postOfficeMapper, CounterpartyMapper counterpartyMapper,
            ShipmentTrackingDetailMapper shipmentTrackingDetailMapper) {
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
        //create ParcelItem
        List<ParcelItem> parcelItemsPack1 = new ArrayList<>();
        ParcelItem parcelItem1 = new ParcelItem("Sugar", 2, 0.3f, new BigDecimal("3.0"));
        ParcelItem parcelItem2 = new ParcelItem("Salt", 1, 0.2f, new BigDecimal("3.5"));
        parcelItemsPack1.add(parcelItem1);
        parcelItemsPack1.add(parcelItem2);

        List<ParcelItem> parcelItemsPack2 = new ArrayList<>();
        ParcelItem parcelItem3 = new ParcelItem("Rice", 1, 0.3f, new BigDecimal("4.0"));
        ParcelItem parcelItem4 = new ParcelItem("Buckwheat", 1, 0.2f, new BigDecimal("4.0"));
        parcelItemsPack2.add(parcelItem3);
        parcelItemsPack2.add(parcelItem4);

        List<ParcelItem> parcelItemsPack3 = new ArrayList<>();
        ParcelItem parcelItem5 = new ParcelItem("Potatoes", 1, 0.5f, new BigDecimal("4.0"));
        ParcelItem parcelItem6 = new ParcelItem("Tomatoes", 1, 0.5f, new BigDecimal("5.5"));
        parcelItemsPack2.add(parcelItem5);
        parcelItemsPack2.add(parcelItem6);

        List<ParcelItem> parcelItemsPack4 = new ArrayList<>();
        ParcelItem parcelItem7 = new ParcelItem("Pepper", 1, 0.5f, new BigDecimal("5.0"));
        ParcelItem parcelItem8 = new ParcelItem("Corn", 1, 0.5f, new BigDecimal("5.0"));
        parcelItemsPack4.add(parcelItem7);
        parcelItemsPack4.add(parcelItem8);

        List<ParcelItem> parcelItemsPack5 = new ArrayList<>();
        ParcelItem parcelItem9 = new ParcelItem("Limes", 1, 1f, new BigDecimal("2.0"));
        ParcelItem parcelItem10 = new ParcelItem("Oranges", 1, 1f, new BigDecimal("2.5"));
        parcelItemsPack5.add(parcelItem9);
        parcelItemsPack5.add(parcelItem10);

        List<ParcelItem> parcelItemsPack6 = new ArrayList<>();
        ParcelItem parcelItem11 = new ParcelItem("Cucumbers", 1, 0.5f, new BigDecimal("2.0"));
        ParcelItem parcelItem12 = new ParcelItem("Eggplants", 1, 0.5f, new BigDecimal("2.0"));
        parcelItemsPack5.add(parcelItem11);
        parcelItemsPack5.add(parcelItem12);

        //create Parcel
        List<Parcel> parcelsForShipment1 = new ArrayList<>();
        Parcel parcel1 = new Parcel(0.5f, 0.5f, new BigDecimal("6.5"),
                new BigDecimal("1.5"), parcelItemsPack1);
        Parcel parcel2 = new Parcel(0.5f, 0.5f, new BigDecimal("8.0"),
                new BigDecimal("1.0"), parcelItemsPack2);
        parcelsForShipment1.add(parcel1);
        parcelsForShipment1.add(parcel2);

        List<Parcel> parcelsForShipment2 = new ArrayList<>();
        Parcel parcel3 = new Parcel(1, 1, new BigDecimal("9.5"),
                new BigDecimal("0.3"), parcelItemsPack3);
        Parcel parcel4 = new Parcel(1, 1, new BigDecimal("10.0"),
                new BigDecimal("0.2"), parcelItemsPack4);
        parcelsForShipment2.add(parcel3);
        parcelsForShipment2.add(parcel4);

        List<Parcel> parcelsForShipment3 = new ArrayList<>();
        Parcel parcel5 = new Parcel(2, 2, new BigDecimal("4.5"),
                new BigDecimal("1.25"), parcelItemsPack5);
        Parcel parcel6 = new Parcel(1, 1, new BigDecimal("4.0"),
                new BigDecimal("1.0"), parcelItemsPack6);
        parcelsForShipment3.add(parcel5);
        parcelsForShipment3.add(parcel6);

        // create Shipment
        List<ShipmentDto> shipmentsSaved = new ArrayList<>();
        Shipment shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(1), DeliveryType.W2W, 
                new BigDecimal("15"), parcelsForShipment1);
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));
        shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(0), DeliveryType.W2D,
                new BigDecimal("20.5"), parcelsForShipment2);
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));
        shipment = new Shipment(clientsSaved.get(1), clientsSaved.get(0), DeliveryType.D2D,
                new BigDecimal("13.5"), parcelsForShipment3);
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));

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
