package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Shipment;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ShipmentService;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class ShipmentControllerIT extends BaseControllerIT {
    private static final String SHIPMENTS = "/shipments";
    private static final String SHIPMENTS_ID = "shipments/{id}";
    private static final String ID = "id";
    private static final String SENDER_ID = "senderId";
    private static final String JSON_SHIPMENT = "json/shipment.json";
    private static final String RECIPIENT_ID = "recipientId";
    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";
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
                get(SHIPMENTS_ID, shipmentId).
                then().
                statusCode(SC_OK).
                body(ID, equalTo(shipmentId));
    }

    @Test
    public void getShipment_notFound() throws Exception {
        when().
                get(SHIPMENTS_ID, shipmentId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createClient() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile(JSON_SHIPMENT);
        jsonObject.put(SENDER_ID, (int) testHelper.createClient().getId());
        jsonObject.put(RECIPIENT_ID, (int) testHelper.createClient().getId());
        String expectedJson = jsonObject.toString();

        int newShipmentId =
                given().
                        contentType(APPLICATION_JSON).
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
        JSONObject jsonObject = testHelper.getJsonObjectFromFile(JSON_SHIPMENT);
        jsonObject.put(SENDER_ID, (int) testHelper.createClient().getId());
        jsonObject.put(RECIPIENT_ID, (int) testHelper.createClient().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType(APPLICATION_JSON).
                body(expectedJson).
                when().
                put(SHIPMENTS_ID, shipmentId).
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
                delete(SHIPMENTS_ID, shipmentId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteShipment_notFound() throws Exception {
        when().
                delete(SHIPMENTS_ID, shipmentId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }
}