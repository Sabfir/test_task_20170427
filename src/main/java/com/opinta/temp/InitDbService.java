package com.opinta.temp;

import com.opinta.dto.AddressDto;
import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.dto.CounterpartyDto;
import com.opinta.dto.ParcelDto;
import com.opinta.dto.ParcelItemDto;
import com.opinta.dto.PostOfficeDto;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Address;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.PostOffice;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentStatus;
import com.opinta.entity.ShipmentTrackingDetail;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;
import com.opinta.mapper.AddressMapper;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import com.opinta.mapper.ClientMapper;
import com.opinta.mapper.CounterpartyMapper;
import com.opinta.mapper.ParcelItemMapper;
import com.opinta.mapper.ParcelMapper;
import com.opinta.mapper.PostOfficeMapper;
import com.opinta.mapper.PostcodePoolMapper;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.mapper.ShipmentTrackingDetailMapper;
import com.opinta.service.AddressService;
import com.opinta.service.ClientService;
import com.opinta.service.CounterpartyService;
import com.opinta.service.ParcelItemService;
import com.opinta.service.ParcelService;
import com.opinta.service.PostOfficeService;
import com.opinta.service.PostcodePoolService;
import com.opinta.service.ShipmentService;
import com.opinta.service.ShipmentTrackingDetailService;
import com.opinta.service.TariffGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
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
    private final ParcelService parcelService;
    private final ParcelItemService parcelItemService;
    private final ParcelItemMapper parcelItemMapper;
    private final ParcelMapper parcelMapper;
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
            PostcodePoolService postcodePoolService, ClientService clientService,
            AddressService addressService, ShipmentService shipmentService,
            CounterpartyService counterpartyService, PostOfficeService postOfficeService,
            ShipmentTrackingDetailService shipmentTrackingDetailService,
            TariffGridService tariffGridService, ParcelService parcelService,
            ParcelItemService parcelItemService, ParcelItemMapper parcelItemMapper,
            ParcelMapper parcelMapper, ClientMapper clientMapper, AddressMapper addressMapper,
            PostcodePoolMapper postcodePoolMapper, ShipmentMapper shipmentMapper,
            BarcodeInnerNumberMapper barcodeInnerNumberMapper,
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
        this.parcelService = parcelService;
        this.parcelItemService = parcelItemService;
        this.parcelItemMapper = parcelItemMapper;
        this.parcelMapper = parcelMapper;
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
        PostcodePoolDto postcodePoolDto = postcodePoolMapper
                .toDto(new PostcodePool("00001", false));
        final long postcodePoolId = postcodePoolService.save(postcodePoolDto).getId();

        List<BarcodeInnerNumberDto> barcodeInnerNumbers = new ArrayList<>();
        barcodeInnerNumbers.add(barcodeInnerNumberMapper
                .toDto(new BarcodeInnerNumber("0000001", USED)));
        barcodeInnerNumbers.add(barcodeInnerNumberMapper
                .toDto(new BarcodeInnerNumber("0000002", RESERVED)));
        barcodeInnerNumbers.add(barcodeInnerNumberMapper
                .toDto(new BarcodeInnerNumber("0000003", RESERVED)));

        postcodePoolService.addBarcodeInnerNumbers(postcodePoolId, barcodeInnerNumbers);

        // create Address
        List<AddressDto> addresses = new ArrayList<>();
        List<AddressDto> addressesSaved = new ArrayList<>();
        addresses.add(addressMapper.toDto(new Address("00001", "Ternopil", "Monastiriska",
                "Monastiriska", "Sadova", "51", "")));
        addresses.add(addressMapper.toDto(new Address("00002", "Kyiv", "",
                "Kyiv", "Khreschatik", "121", "37")));
        addresses.forEach(addressDto -> addressesSaved.add(addressService.save(addressDto)));

        // create Client with Counterparty
        PostcodePoolDto postcodePoolDto1 = postcodePoolMapper
                .toDto(new PostcodePool("00003", false));
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
        clients.forEach(client -> clientsSaved.add(this.clientMapper
                .toEntity(clientService.save(this.clientMapper.toDto(client))))
        );

        // create ParcelItem
        List<ParcelItemDto> parcelItemsSaved = new ArrayList<>();
        ParcelItem parcelItem = new ParcelItem("first item", 1, 1, new BigDecimal(1));
        parcelItemsSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem)));
        parcelItem = new ParcelItem("second item", 1, 2, new BigDecimal(2));
        parcelItemsSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem)));
        parcelItem = new ParcelItem("third item", 1, 3, new BigDecimal(3));
        parcelItemsSaved.add(parcelItemService.save(parcelItemMapper.toDto(parcelItem)));

        // create Parcel
        List<ParcelDto> parcelsSaved = new ArrayList<>();
        Parcel parcel = new Parcel(1, 1, new BigDecimal("12.5"), new BigDecimal("2.5"));
        parcel.setParcelItems(parcelItemMapper.toEntity(Arrays.asList(parcelItemsSaved.get(0))));
        parcelsSaved.add(parcelService.save(parcelMapper.toDto(parcel)));
        parcel = new Parcel(2, 2, new BigDecimal("19.5"), new BigDecimal("0.5"));
        parcel.setParcelItems(parcelItemMapper.toEntity(Arrays.asList(parcelItemsSaved.get(1))));
        parcelsSaved.add(parcelService.save(parcelMapper.toDto(parcel)));
        parcel = new Parcel(3, 3, new BigDecimal("8.5"), new BigDecimal("2.25"));
        parcel.setParcelItems(parcelItemMapper.toEntity(Arrays.asList(parcelItemsSaved.get(2))));
        parcelsSaved.add(parcelService.save(parcelMapper.toDto(parcel)));

        // create Shipment
        List<ShipmentDto> shipmentsSaved = new ArrayList<>();
        Shipment shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(1),
                DeliveryType.W2W, new BigDecimal("15"));
        shipment.setParcels(parcelMapper.toEntity(Arrays.asList(parcelsSaved.get(0))));
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));
        shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(0),
                DeliveryType.W2D, new BigDecimal("20.5"));
        shipment.setParcels(parcelMapper.toEntity(Arrays.asList(parcelsSaved.get(1))));
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));
        shipment = new Shipment(clientsSaved.get(1), clientsSaved.get(0),
                DeliveryType.D2D, new BigDecimal("13.5"));
        shipment.setParcels(parcelMapper.toEntity(Arrays.asList(parcelsSaved.get(2))));
        shipmentsSaved.add(shipmentService.save(shipmentMapper.toDto(shipment)));

        // create PostOffice
        PostcodePoolDto postcodePoolDto2 = postcodePoolMapper
                .toDto(new PostcodePool("00002", false));
        PostcodePoolDto postcodePoolDtoSaved = postcodePoolService.save(postcodePoolDto2);
        PostOffice postOffice = new PostOffice("Lviv post office",
                addressMapper.toEntity(addressesSaved.get(0)),
                postcodePoolMapper.toEntity(postcodePoolDtoSaved));
        PostOfficeDto postOfficeSaved = postOfficeService.save(postOfficeMapper.toDto(postOffice));

        // create ShipmentTrackingDetail
        ShipmentTrackingDetail shipmentTrackingDetail =
                new ShipmentTrackingDetail(shipmentMapper.toEntity(shipmentsSaved.get(0)),
                        postOfficeMapper.toEntity(postOfficeSaved),
                        ShipmentStatus.PREPARED, new Date());
        shipmentTrackingDetailService.save(shipmentTrackingDetailMapper
                .toDto(shipmentTrackingDetail));
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
