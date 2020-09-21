package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Shipment;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ShipmentService;
import integration.helper.TestHelper;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class ShipmentControllerIT extends BaseControllerIT {
    public static final String SHIPMENTS = "/shipments";
    public static final String SHIPMENT_ID = "/shipments/{id}";
    public static final String ID = "id";
    public static final String JSON_FILE = "json/shipment.json";
    public static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private Shipment shipment;
    private int shipmentId = MIN_VALUE;
    @Autowired
    private ShipmentMapper shipmentMapper;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private TestHelper testHelper;

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
    public void getShipments() throws Exception {
        when().
                get(SHIPMENTS).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getShipment() throws Exception {
        when().
                get(SHIPMENT_ID, shipmentId).
        then().
                statusCode(SC_OK).
                body(ID, equalTo(shipmentId));
    }

    @Test
    public void getShipment_notFound() throws Exception {
        when().
                get(SHIPMENT_ID, shipmentId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createClient() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile(JSON_FILE);
        jsonObject.put("senderId", (int) testHelper.createClient().getId());
        jsonObject.put("recipientId", (int) testHelper.createClient().getId());
        jsonObject.put("price", 45);
        String expectedJson = jsonObject.toString();

        int newShipmentId =
                given().
                        contentType(CONTENT_TYPE).
                        body(expectedJson).
                when().
                        post(SHIPMENTS).
                then().
                        extract().
                        path(ID);

        // check created data
        Shipment createdShipment = shipmentService.getEntityById(newShipmentId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateShipment() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile(JSON_FILE);
        jsonObject.put("senderId", (int) testHelper.createClient().getId());
        jsonObject.put("recipientId", (int) testHelper.createClient().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType(CONTENT_TYPE).
                body(expectedJson).
        when().
                put(SHIPMENT_ID, shipmentId).
        then().
                statusCode(SC_OK);

        // check updated data
        ShipmentDto shipmentDto = shipmentMapper.toDto(shipmentService.getEntityById(shipmentId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentDto);

        jsonObject.put("price", 45);
        expectedJson = jsonObject.toString();

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteShipment() throws Exception {
        when().
                delete(SHIPMENT_ID, shipmentId).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteShipment_notFound() throws Exception {
        when().
                delete(SHIPMENT_ID, shipmentId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
