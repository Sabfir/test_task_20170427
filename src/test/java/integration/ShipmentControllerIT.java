package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Shipment;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ShipmentService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.ArrayValueMatcher;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class ShipmentControllerIT extends BaseControllerIT {
    private static final String SHIPMENTS_URL = "/shipments";
    private static final String ID_FIELD = "id";
    private static final String SHIPMENT_GET_URL = "/shipments/{id}";
    private static final String SHIPMENT_JSON = "json/shipment.json";
    private static final String PARCEL_ONE_JSON = "json/parcel1.json";
    private static final String PARCEL_TWO_JSON = "json/parcel2.json";
    private static final String PARCEL_ITEM_ONE_JSON = "json/parcelItem1.json";
    private static final String PARCEL_ITEM_TWO_JSON = "json/parcelItem2.json";
    private static final String PARCEL_ITEMS = "parcelItems";
    private static final String SENDER_ID = "senderId";
    private static final String RECIPIENT_ID = "recipientId";
    private static final String PARCELS = "parcels";
    private static final String APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";
    private static final String PARCELS_IGNORED_ID = "parcels[*].id";
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
                get(SHIPMENTS_URL).
        then().
                statusCode(SC_OK)
        .extract().body().jsonPath().prettyPrint();
    }

    @Test
    public void getShipment() throws Exception {
        when().
                get("shipments/{id}", shipmentId).
        then().
                statusCode(SC_OK).
                body(ID_FIELD, equalTo(shipmentId))
        .extract().body().jsonPath().prettyPrint();
    }

    @Test
    public void getShipment_notFound() throws Exception {
        when().
                get(SHIPMENT_GET_URL, shipmentId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createClient() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile(SHIPMENT_JSON);
        JSONArray parcels = new JSONArray();
        JSONObject parcel1 = testHelper.getJsonObjectFromFile(PARCEL_ONE_JSON);
        JSONObject parcel2 = testHelper.getJsonObjectFromFile(PARCEL_TWO_JSON);

        JSONArray parcelItems = new JSONArray();
        JSONObject parcelItem1 = testHelper.getJsonObjectFromFile(PARCEL_ITEM_ONE_JSON);
        parcelItems.add(parcelItem1);
        parcel1.put(PARCEL_ITEMS, parcelItems);
        JSONObject parcelItem2 = testHelper.getJsonObjectFromFile(PARCEL_ITEM_TWO_JSON);
        parcelItems.add(parcelItem2);
        JSONArray parcelItemsOther = new JSONArray();
        parcelItemsOther.add(parcelItem1);
        parcelItemsOther.add(parcelItem2);
        parcel2.put(PARCEL_ITEMS, parcelItemsOther);

        parcels.add(parcel2);
        parcels.add(parcel1);

        jsonObject.put(SENDER_ID, (int) testHelper.createClient().getId());
        jsonObject.put(RECIPIENT_ID, (int) testHelper.createClient().getId());
        jsonObject.put(PARCELS, parcels);

        String expectedJson = jsonObject.toString();

        int newShipmentId =
                given().
                        contentType(APPLICATION_JSON_UTF_8).
                        body(expectedJson).
                when().
                        post(SHIPMENTS_URL).
                then().
                        extract().
                        path(ID_FIELD);

        // check created data
        Shipment createdShipment = shipmentService.getEntityById(newShipmentId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));

        ArrayValueMatcher<Object> arrValMatch = new ArrayValueMatcher<>(new CustomComparator(
                JSONCompareMode.LENIENT, new Customization(PARCELS_IGNORED_ID, (o1, o2) -> true))
        );
        Customization arrayValueMatchCustomization = new Customization(PARCELS, arrValMatch);
        CustomComparator customComparator = new CustomComparator(JSONCompareMode.LENIENT, arrayValueMatchCustomization);

        JSONAssert.assertEquals(expectedJson, actualJson, customComparator);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateShipment() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile(SHIPMENT_JSON);
        JSONArray parcels = new JSONArray();
        JSONObject parcel1 = testHelper.getJsonObjectFromFile(PARCEL_ONE_JSON);
        JSONObject parcel2 = testHelper.getJsonObjectFromFile(PARCEL_TWO_JSON);

        JSONArray parcelItems = new JSONArray();
        JSONObject parcelItem1 = testHelper.getJsonObjectFromFile(PARCEL_ITEM_ONE_JSON);
        parcelItems.add(parcelItem1);
        parcel1.put(PARCEL_ITEMS, parcelItems);
        JSONObject parcelItem2 = testHelper.getJsonObjectFromFile(PARCEL_ITEM_TWO_JSON);
        parcelItems.add(parcelItem2);
        JSONArray parcelItemsOther = new JSONArray();
        parcelItemsOther.add(parcelItem1);
        parcelItemsOther.add(parcelItem2);
        parcel2.put(PARCEL_ITEMS, parcelItemsOther);

        parcels.add(parcel2);
        parcels.add(parcel1);

        jsonObject.put(SENDER_ID, (int) testHelper.createClient().getId());
        jsonObject.put(RECIPIENT_ID, (int) testHelper.createClient().getId());
        jsonObject.put(PARCELS, parcels);

        String expectedJson = jsonObject.toString();

        given().
                contentType(APPLICATION_JSON_UTF_8).
                body(expectedJson).
        when().
                put(SHIPMENT_GET_URL, shipmentId).
        then().
                statusCode(SC_OK);

        // check updated data
        ShipmentDto shipmentDto = shipmentMapper.toDto(shipmentService.getEntityById(shipmentId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentDto);

        jsonObject.put("price", 138);
        expectedJson = jsonObject.toString();

        ArrayValueMatcher<Object> arrValMatch = new ArrayValueMatcher<>(new CustomComparator(
                JSONCompareMode.LENIENT, new Customization(PARCELS_IGNORED_ID, (o1, o2) -> true))
        );
        Customization arrayValueMatchCustomization = new Customization(PARCELS, arrValMatch);
        CustomComparator customComparator = new CustomComparator(JSONCompareMode.LENIENT, arrayValueMatchCustomization);

        JSONAssert.assertEquals(expectedJson, actualJson, customComparator);
    }

    @Test
    public void deleteShipment() throws Exception {
        when().
                delete(SHIPMENT_GET_URL, shipmentId).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteShipment_notFound() throws Exception {
        when().
                delete(SHIPMENT_GET_URL, shipmentId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
