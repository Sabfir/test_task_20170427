package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ParcelDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.Shipment;
import com.opinta.mapper.ParcelMapper;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ParcelService;
import com.opinta.service.ShipmentService;
import integration.helper.TestHelper;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_OK;

public class ParcelIT extends BaseControllerIT {
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private ShipmentMapper shipmentMapper;
    @Autowired
    private ParcelMapper parcelMapper;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private TestHelper testHelper;

    private Shipment shipment;
    private int shipmentId = MIN_VALUE;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        shipment = testHelper.createShipment();
        shipmentId = (int) shipment.getId();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteShipment(shipment);
    }

    @Test
    public void getParcels() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(parcelMapper.toDto(shipment.getParcels()));

        List<ParcelDto> parcels = parcelService.getAllByShipment(shipment);
        String actualJson = objectMapper.writeValueAsString(parcels);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void getParcel() throws Exception {
        Parcel currentParcel = shipment.getParcels().get(0);
        String expectedJson = objectMapper.writeValueAsString(parcelMapper.toDto(currentParcel));

        ParcelDto parcel = parcelService.getById(currentParcel.getId());
        String actualJson = objectMapper.writeValueAsString(parcel);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void addParcel() throws Exception {
        Parcel newParcel = new Parcel(3, 2, 0.5f, 0.2f, new BigDecimal(10), new BigDecimal(36));
        ParcelItem item = new ParcelItem("pan", 2, 1.5f, 5);
        newParcel.addItem(item);
        shipment.addParcel(newParcel);
        String expectedJson = objectMapper.writeValueAsString(parcelMapper.toDto(shipment.getParcels()));

        ShipmentDto updatedShipment = shipmentService.update(shipmentId, shipmentMapper.toDto(shipment));
        String actualJson = objectMapper.writeValueAsString(updatedShipment.getParcels());

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void updateParcel() throws Exception {
        Parcel currentParcel = shipment.getParcels().get(0);
        currentParcel.setWeight(3);
        //currentParcel.setPrice(?);
        String expectedJson = objectMapper.writeValueAsString(parcelMapper.toDto(currentParcel));

        ShipmentDto updatedShipment = shipmentService.update(shipmentId, shipmentMapper.toDto(shipment));
        String actualJson = objectMapper.writeValueAsString(updatedShipment.getParcels().get(0));

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void updateParcels() throws Exception {
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/edit_parcels.json");
        jsonObject.put("senderId", (int) testHelper.createClient().getId());
        jsonObject.put("recipientId", (int) testHelper.createClient().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
                when().
                put("/shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK);

        // check updated data
        ShipmentDto shipmentDto = shipmentMapper.toDto(shipmentService.getEntityById(shipmentId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteParcel() throws Exception {
        Parcel currentParcel = shipment.getParcels().get(0);
        shipment.removeParcel(currentParcel);
        String expectedJson = objectMapper.writeValueAsString(parcelMapper.toDto(shipment.getParcels()));

        ShipmentDto updatedShipment = shipmentService.update(shipmentId, shipmentMapper.toDto(shipment));
        String actualJson = objectMapper.writeValueAsString(updatedShipment.getParcels());

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }
}
