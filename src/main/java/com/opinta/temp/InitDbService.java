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
        addresses.add(addressMapper.toDto(new Address("00001", "Ternopil", "Monastiriska",
                "Monastiriska", "Sadova", "51", "")));
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
        Shipment shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(1), DeliveryType.W2W,
                new BigDecimal(1), new BigDecimal(1));

        List<ParcelItem> parcelItemDtos1 = new ArrayList<>();
        parcelItemDtos1.add(new ParcelItem("Apple laptop", 1.0f,
                3.456f, new BigDecimal("3456.3")));
        parcelItemDtos1.add(new ParcelItem("Keyboard", 1.0f, 0.270f,
                new BigDecimal("142.34")));
        parcelItemDtos1.add(new ParcelItem("Vertical Game Mouse", 1.0f, 0.456f,
                new BigDecimal("459.99")));
        parcelItemDtos1.add(new ParcelItem("Spring 4 book", 1.0f, 0.657f,
                new BigDecimal("756.3")));

        List<ParcelItem> parcelItemDtos2 = new ArrayList<>();
        parcelItemDtos2.add(new ParcelItem("Citric acid", 0.5f, 0.678f,
                new BigDecimal("140.56")));
        parcelItemDtos2.add(new ParcelItem("Potassium oxide", 0.1f, 0.34f,
                new BigDecimal("160.56")));

        List<ParcelItem> parcelItemDtos3 = new ArrayList<>();
        parcelItemDtos3.add(new ParcelItem("Vollyball", 1.0f, 0.345f,
                new BigDecimal("245.5")));
        parcelItemDtos3.add(new ParcelItem("Basketball", 1f, 0.647f,
                new BigDecimal("364.2")));

        List<Parcel> parcels = new ArrayList<>();
        parcels.add(new Parcel(4.876f, 12.45f, 45.32f, 0.57f,
                new BigDecimal("4230.33"), new BigDecimal("4500.00"), parcelItemDtos1));
        parcels.add(new Parcel(1.0f, 0.25f, 0.25f, 0.25f,
                new BigDecimal("301.12"), new BigDecimal("310.00"), parcelItemDtos2));
        parcels.add(new Parcel(1.240f, 1.2f, 2.4f, 1.2f, new BigDecimal("609.7"),
                new BigDecimal("630.00"), parcelItemDtos3));

        shipment.setParcels(parcels);

        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));

       parcelItemDtos1 = new ArrayList<>();
        parcelItemDtos1.add(new ParcelItem("Mango dress", 1.0f, 0.356f, new BigDecimal("356.3")));
        parcelItemDtos1.add(new ParcelItem("Laboutin shoues", 1.0f, 0.470f, new BigDecimal("1042.34")));
        parcelItemDtos1.add(new ParcelItem("Versache top", 1.0f, 0.256f, new BigDecimal("800.99")));
        parcelItemDtos1.add(new ParcelItem("Chanel Perfume", 1.0f, 0.250f, new BigDecimal("1756.3")));

        parcelItemDtos2 = new ArrayList<>();
        parcelItemDtos2.add(new ParcelItem("IRF9640STRLPBF Transisator", 10f, 0.18f, new BigDecimal("140.56")));
        parcelItemDtos2.add(new ParcelItem(" IRFB23N15DPBF Transistor", 43f, 0.34f, new BigDecimal("1600.56")));
        parcelItemDtos2.add(new ParcelItem(" BS250FTA Transistor", 14f, 0.610f, new BigDecimal("140.56")));

        parcelItemDtos3 = new ArrayList<>();
        parcelItemDtos3.add(new ParcelItem("Uranium", 1.0f, 0.1f, new BigDecimal("2459939.5")));
        parcelItemDtos3.add(new ParcelItem("Plutonium", 1f, 0.1f, new BigDecimal("3643933.2")));

        parcels = new ArrayList<>();
        parcels.add(new Parcel(1.65f, 0.245f, 0.15f, 0.117f, new BigDecimal("3430.33"),
                new BigDecimal("3600.00"), parcelItemDtos1));
        parcels.add(new Parcel(1f, 0.15f, 0.05f, 0.15f, new BigDecimal("1840.12"),
                new BigDecimal("1900.00"), parcelItemDtos2));
        parcels.add(new Parcel(1.240f, 1.2f, 2.4f, 1.2f, new BigDecimal("6103872.70"),
                new BigDecimal("6103872.70"), parcelItemDtos3));
       shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(0),
                DeliveryType.D2D, new BigDecimal(200), new BigDecimal(205));
        shipment.setParcels(parcels);
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));
        shipment = new Shipment(clientsSaved.get(1), clientsSaved.get(0), DeliveryType.D2D,new BigDecimal("2.25"),
                new BigDecimal("13.5"));
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
