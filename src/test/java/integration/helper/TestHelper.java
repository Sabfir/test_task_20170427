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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.opinta.service.AddressService;
import com.opinta.service.ClientService;
import com.opinta.service.CounterpartyService;
import com.opinta.service.ParcelItemService;
import com.opinta.service.ParcelService;
import com.opinta.service.PostOfficeService;
import com.opinta.service.PostcodePoolService;
import com.opinta.service.ShipmentService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestHelper {
    private static final String MONASTIRISKA = "Monastiriska";
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
        Shipment shipment = new Shipment(createClient(), createClient(),
                DeliveryType.D2D, new BigDecimal("35.2"), createParcels());
        return shipmentService.saveEntity(shipment);
    }

    public List<ParcelItem> createParcelItems1() {
        List<ParcelItem> parcelItems = new ArrayList<>();
        List<ParcelItem> savedParcelItems = new ArrayList<>();
        parcelItems.add(new ParcelItem("Item1", 2, 0.2F, BigDecimal.TEN));
        parcelItems.add(new ParcelItem("Item2", 5, 1.0F, new BigDecimal("250")));
        parcelItems.forEach(parcelItem -> savedParcelItems.add(parcelItemService.saveEntity(parcelItem)));
        return savedParcelItems;
    }

    public List<ParcelItem> createParcelItems2() {
        List<ParcelItem> parcelItems = new ArrayList<>();
        List<ParcelItem> savedParcelItems = new ArrayList<>();
        parcelItems.add(new ParcelItem("Item3", 3, 0.4F, new BigDecimal("101")));
        parcelItems.add(new ParcelItem("Item4", 1, 0.4F, new BigDecimal("1000")));
        parcelItems.forEach(parcelItem -> savedParcelItems.add(parcelItemService.saveEntity(parcelItem)));
        return savedParcelItems;
    }

    public List<Parcel> createParcels() {
        List<Parcel> parcels = new ArrayList<>();
        List<Parcel> savedParcels = new ArrayList<>();
        parcels.add(new Parcel(5F, 4F, new BigDecimal("100"), new BigDecimal("25"), createParcelItems1()));
        parcels.add(new Parcel(3F, 5F, new BigDecimal("200"), new BigDecimal("30"), createParcelItems2()));
        parcels.forEach(parcel -> savedParcels.add(parcelService.saveEntity(parcel)));
        return savedParcels;
    }

    public void deleteShipment(Shipment shipment) {
        shipmentService.delete(shipment.getId());
        clientService.delete(shipment.getSender().getId());
        clientService.delete(shipment.getRecipient().getId());
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
