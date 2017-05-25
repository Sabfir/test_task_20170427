package integration.helper;

import com.opinta.entity.*;
import com.opinta.service.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestHelper {
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

    public PostOffice createPostOffice() {
        PostOffice postOffice = new PostOffice("Lviv post office", createAddress(), createPostcodePool());
        return postOfficeService.saveEntity(postOffice);
    }

    public void deletePostOffice(PostOffice postOffice) {
        postOfficeService.delete(postOffice.getId());
        postcodePoolService.delete(postOffice.getPostcodePool().getId());
    }

    public Shipment createShipment() {

        List<ParcelItem> parcelItemDtos1 = new ArrayList<>();
        parcelItemDtos1.add(new ParcelItem("House of Toys", 2.0f, 0.356f, new BigDecimal("400.0")));
        parcelItemDtos1.add(new ParcelItem("NA-NA Toy Store", 3.0f, 0.470f, new BigDecimal("1042.34")));
        parcelItemDtos1.add(new ParcelItem("Super Kids", 5.0f, 0.256f, new BigDecimal("800.99")));
        parcelItemDtos1.add(new ParcelItem("Kinder-shop", 1.0f, 0.250f, new BigDecimal("1756.3")));

        List<ParcelItem> parcelItemDtos2 = new ArrayList<>();
        parcelItemDtos2.add(new ParcelItem("Designer LEGO City Passenger Terminal (60104)", 10f, 0.18f, new BigDecimal("140.56")));
        parcelItemDtos2.add(new ParcelItem("Designer LEGO City Swift pursuit 294 details (60138)", 43f, 0.34f, new BigDecimal("1600.56")));
        parcelItemDtos2.add(new ParcelItem("Designer LEGO City Servicing of VIPs (60102)", 14f, 0.610f, new BigDecimal("140.56")));

        List<ParcelItem> parcelItemDtos3 = new ArrayList<>();
        parcelItemDtos3.add(new ParcelItem("Designer LEGO BIONICLE Storm Monster (71314)", 1.0f, 0.155f, new BigDecimal("245.5")));
        parcelItemDtos3.add(new ParcelItem("Designer LEGO Star Wars Imperial Deathtroat 106 details (75121)", 1f, 0.155f, new BigDecimal("364.2")));

        List<Parcel> parcels = new ArrayList<>();
        parcels.add(new Parcel(1.65f, 0.245f, 0.15f, 0.117f, new BigDecimal("3430.33"), new BigDecimal("33.00"), parcelItemDtos1));
        parcels.add(new Parcel(1f, 0.15f, 0.05f, 0.15f, new BigDecimal("1840.12"), new BigDecimal("42"), parcelItemDtos2));
        parcels.add(new Parcel(1.240f, 1.2f, 2.4f, 1.2f, new BigDecimal("6103.70"), new BigDecimal("30"), parcelItemDtos3));

        Shipment shipment = new Shipment(createClient(), createClient(), DeliveryType.D2D, new BigDecimal(200), new BigDecimal(205));
        shipment.setParcels(parcels);
        return shipmentService.saveEntity(shipment);
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
        Address address = new Address("00001", "Ternopil", "Monastiriska", "Monastiriska", "Sadova", "51", "");
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
