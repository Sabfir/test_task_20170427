package com.opinta.temp;

import com.opinta.dto.*;
import com.opinta.entity.*;
import com.opinta.mapper.AddressMapper;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import com.opinta.mapper.ClientMapper;
import com.opinta.mapper.CounterpartyMapper;
import com.opinta.mapper.PostOfficeMapper;
import com.opinta.mapper.PostcodePoolMapper;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.mapper.ShipmentTrackingDetailMapper;
import com.opinta.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

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
    private ParcelService parcelService;
    private ParcelItemService parcelItemService;

    private ClientMapper clientMapper;
    private AddressMapper addressMapper;
    private PostcodePoolMapper postcodePoolMapper;
    private BarcodeInnerNumberMapper barcodeInnerNumberMapper;
    private ShipmentMapper shipmentMapper;
    private PostOfficeMapper postOfficeMapper;
    private CounterpartyMapper counterpartyMapper;
    private ShipmentTrackingDetailMapper shipmentTrackingDetailMapper;

    @Autowired
    public InitDbService(BarcodeInnerNumberService barcodeInnerNumberService, PostcodePoolService postcodePoolService,
                         ClientService clientService, AddressService addressService, ShipmentService shipmentService,
                         CounterpartyService counterpartyService, PostOfficeService postOfficeService,
                         ShipmentTrackingDetailService shipmentTrackingDetailService, TariffGridService tariffGridService,
                         ParcelService parcelService, ParcelItemService parcelItemService, ClientMapper clientMapper,
                         AddressMapper addressMapper, PostcodePoolMapper postcodePoolMapper, BarcodeInnerNumberMapper barcodeInnerNumberMapper,
                         ShipmentMapper shipmentMapper, PostOfficeMapper postOfficeMapper, CounterpartyMapper counterpartyMapper,
                         ShipmentTrackingDetailMapper shipmentTrackingDetailMapper) {
        this.barcodeInnerNumberService = barcodeInnerNumberService;
        this.postcodePoolService = postcodePoolService;
        this.clientService = clientService;
        this.addressService = addressService;
        this.shipmentService = shipmentService;
        this.counterpartyService = counterpartyService;
        this.postOfficeService = postOfficeService;
        this.shipmentTrackingDetailService = shipmentTrackingDetailService;
        this.tariffGridService = tariffGridService;
        this.parcelService = parcelService;
        this.parcelItemService = parcelItemService;
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



        // create Shipment
        List<ShipmentDto> shipmentsSaved = new ArrayList<>();
        Shipment shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(1), DeliveryType.W2W, new BigDecimal("15"));
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));
        shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(0), DeliveryType.W2D, new BigDecimal("20.5"));
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));
        shipment = new Shipment(clientsSaved.get(1), clientsSaved.get(0), DeliveryType.D2D, new BigDecimal("13.5"));
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));

        // create ParcelItem
        ParcelItem parcelItem1 = new ParcelItem("FistItem", 1, 1F, new BigDecimal("2.5"));
        ParcelItem parcelItem2 = new ParcelItem("SecondItem", 2, 1F, new BigDecimal("3.3"));
        ParcelItem parcelItem3 = new ParcelItem("ThirdItem", 3, 1F, new BigDecimal("4.2"));
        ParcelItem parcelItem4 = new ParcelItem("FourthItem", 4, 1F, new BigDecimal("5.1"));
        ParcelItem parcelItem5 = new ParcelItem("FifthItem", 4, 1F, new BigDecimal("6.1"));
        ParcelItem parcelItem6 = new ParcelItem("SixthItem", 4, 1F, new BigDecimal("7.1"));
        ParcelItem parcelItem7 = new ParcelItem("SeventhItem", 4, 1F, new BigDecimal("7.1"));
        ParcelItem parcelItem8 = new ParcelItem("EightItem", 4, 1F, new BigDecimal("7.1"));
        ParcelItem parcelItem9 = new ParcelItem("NinthItem", 4, 1F, new BigDecimal("7.1"));
        ParcelItem parcelItem10 = new ParcelItem("TenthItem", 4, 1F, new BigDecimal("7.1"));

        // create Parcel
        Parcel parcel1 = new Parcel(1F, 2F, new BigDecimal("5.8"), new BigDecimal("10"));
        parcel1.setParcelItems(Arrays.asList(parcelItem1, parcelItem2));
        parcel1.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(0)));
        parcelService.saveEntity(parcel1);
        Parcel parcel2 = new Parcel(2F, 4F, new BigDecimal("15.4"), new BigDecimal("20"));
        parcel2.setParcelItems(Arrays.asList(parcelItem3, parcelItem4, parcelItem5));
        parcel2.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(1)));
        parcelService.saveEntity(parcel2);
        Parcel parcel3 = new Parcel(3F, 6F, new BigDecimal("7.1"), new BigDecimal("30"));
        parcel3.setParcelItems(Collections.singletonList(parcelItem6));
        parcel3.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(1)));
        parcelService.saveEntity(parcel3);
        Parcel parcel4 = new Parcel(3F, 6F, new BigDecimal("7.1"), new BigDecimal("30"));
        Parcel parcel5 = new Parcel(3F, 6F, new BigDecimal("7.1"), new BigDecimal("30"));
        parcel4.setParcelItems(Arrays.asList(parcelItem7, parcelItem8));
        parcel5.setParcelItems(Arrays.asList(parcelItem9, parcelItem10));
        parcel4.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(2)));
        parcel5.setShipment(shipmentMapper.toEntity(shipmentsSaved.get(2)));
        parcelService.saveEntity(parcel4);
        parcelService.saveEntity(parcel5);

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
