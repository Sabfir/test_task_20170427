package integration.helper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.opinta.entity.*;
import com.opinta.service.*;
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
        parcelItemDtos1.add(new ParcelItem("Mango dress", 1.0f, 0.356f, new BigDecimal("356.3")));
        parcelItemDtos1.add(new ParcelItem("Laboutin shoues", 1.0f, 0.470f, new BigDecimal("1042.34")));
        parcelItemDtos1.add(new ParcelItem("Versache top", 1.0f, 0.256f, new BigDecimal("800.99")));
        parcelItemDtos1.add(new ParcelItem("Chanel Perfume", 1.0f, 0.250f, new BigDecimal("1756.3")));

        List<ParcelItem> parcelItemDtos2 = new ArrayList<>();
        parcelItemDtos2.add(new ParcelItem("IRF9640STRLPBF Transisator", 10f, 0.18f, new BigDecimal("140.56")));
        parcelItemDtos2.add(new ParcelItem(" IRFB23N15DPBF Transistor", 43f, 0.34f, new BigDecimal("1600.56")));
        parcelItemDtos2.add(new ParcelItem(" BS250FTA Transistor", 14f, 0.610f, new BigDecimal("140.56")));

        List<ParcelItem> parcelItemDtos3 = new ArrayList<>();
        parcelItemDtos3.add(new ParcelItem("Uranium", 1.0f, 0.1f, new BigDecimal("2459939.5")));
        parcelItemDtos3.add(new ParcelItem("Plutonium", 1f, 0.1f, new BigDecimal("3643933.2")));

        List<Parcel> parcels = new ArrayList<>();
        parcels.add(new Parcel(1.65f, 0.245f, 0.15f, 0.117f, new BigDecimal("3430.33"),
                new BigDecimal("33.00"), parcelItemDtos1));
        parcels.add(new Parcel(1f, 0.15f, 0.05f, 0.15f, new BigDecimal("1840.12"),
                new BigDecimal("42"), parcelItemDtos2));
        parcels.add(new Parcel(1.240f, 1.2f, 2.4f, 1.2f, new BigDecimal("6103872.70"),
                new BigDecimal("30"), parcelItemDtos3));
        Shipment shipment = new Shipment(createClient(), createClient(),
                DeliveryType.D2D, new BigDecimal(200), new BigDecimal(205));
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
        Address address = new Address("00001", "Ternopil", "Monastiriska",
                "Monastiriska", "Sadova", "51", "");
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
