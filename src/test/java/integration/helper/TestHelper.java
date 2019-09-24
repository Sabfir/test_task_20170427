package integration.helper;

import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.PostOffice;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import com.opinta.service.AddressService;
import com.opinta.service.ClientService;
import com.opinta.service.CounterpartyService;
import com.opinta.service.ParcelItemService;
import com.opinta.service.ParcelService;
import com.opinta.service.PostOfficeService;
import com.opinta.service.PostcodePoolService;
import com.opinta.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

@Slf4j
@Component
public class TestHelper {
    public static final String MONASTIRISKA = "Monastiriska";
    @Autowired
    private ClientService clientService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CounterpartyService counterpartyService;
    @Autowired
    private PostcodePoolService postcodePoolService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private PostOfficeService postOfficeService;
    @Autowired
    private ParcelItemService parcelItemService;
    @Autowired
    private ParcelService parcelService;

    public PostOffice createPostOffice() {
        PostOffice postOffice = new PostOffice("Lviv post office", createAddress(), createPostcodePool());
        return postOfficeService.saveEntity(postOffice);
    }

    public void deletePostOffice(PostOffice postOffice) {
        postOfficeService.delete(postOffice.getId());
        postcodePoolService.delete(postOffice.getPostcodePool().getId());
    }

    public Shipment createShipment() {
        Shipment shipment = new Shipment(createClient(), createClient(), DeliveryType.D2D, new BigDecimal("5"));
        shipment = shipmentService.saveEntity(shipment);
        ParcelItem parcelItem1 = parcelItemService.saveEntity(createParcelItem());
        ParcelItem parcelItem2 = parcelItemService.saveEntity(createParcelItem());
        ParcelItem parcelItem3 = parcelItemService.saveEntity(createParcelItem());
        Parcel parcel1 = createParcel(parcelItem1, parcelItem2);
        Parcel parcel2 = createParcel(parcelItem3);
        parcel1.setShipment(shipment);
        parcel2.setShipment(shipment);
        parcelService.saveEntity(parcel1);
        parcelService.saveEntity(parcel2);
        shipment = shipmentService.getEntityById(shipment.getId());
        log.info("createShipment {}", shipment);
        return shipment;
    }

    public void deleteShipment(Shipment shipment) {
        shipmentService.delete(shipment.getId());
        clientService.delete(shipment.getSender().getId());
        clientService.delete(shipment.getRecipient().getId());
    }

    public ParcelItem createParcelItem() {
        ParcelItem parcelItem = new ParcelItem("createdInHelper", 1, 1F, new BigDecimal("30"));
        return parcelItemService.saveEntity(parcelItem);
    }

    public Parcel createParcel(ParcelItem... parcelItems) {
        Parcel parcel = new Parcel(1f, 1f, new BigDecimal("30.0"), new BigDecimal("35.2"));
        parcel.setParcelItems(Arrays.asList(parcelItems));
        return parcel;
    }

    public Client createClient() {
        Client newClient = new Client("FOP Ivanov", "001", createAddress(), createCounterparty());
        return clientService.saveEntity(newClient);
    }

    public void deleteClient(Client client) {
        clientService.delete(client.getId());
        addressService.delete(client.getAddress().getId());
        deleteCounterpartyWithPostcodePool(client.getCounterparty());
    }

    public Address createAddress() {
        Address address = new Address("00001", "Ternopil", MONASTIRISKA,
                MONASTIRISKA, "Sadova", "51", "");
        return addressService.saveEntity(address);
    }

    public Counterparty createCounterparty() {
        Counterparty counterparty = new Counterparty("Modna kasta", createPostcodePool());
        return counterpartyService.saveEntity(counterparty);
    }

    public PostcodePool createPostcodePool() {
        return postcodePoolService.saveEntity(new PostcodePool("12345", false));
    }

    public void deleteCounterpartyWithPostcodePool(Counterparty counterparty) {
        counterpartyService.delete(counterparty.getId());
        postcodePoolService.delete(counterparty.getPostcodePool().getId());
    }

    public JSONObject getJsonObjectFromFile(String filePath) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(new FileReader(getFileFromResources(filePath)));
    }

    public String getJsonFromFile(String filePath) throws IOException, ParseException {
        return getJsonObjectFromFile(filePath).toString();
    }

    public File getFileFromResources(String path) {
        return new File(getClass().getClassLoader().getResource(path).getFile());
    }
}
