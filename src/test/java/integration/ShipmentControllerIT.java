package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class ShipmentControllerIT extends BaseControllerIT {
    private Shipment shipment;
    private int shipmentId = MIN_VALUE;
    private Parcel parcel;
    private int parcelId = MIN_VALUE;
    private ParcelItem parcelItem;
    private ObjectMapper mapper = new ObjectMapper();
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
        parcel = shipment.getParcels().get(0);
        parcelId = (int) parcel.getId();
        parcelItem = parcel.getItems().get(0);
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteShipment(shipment);
    }

    @Test
    public void getShipments() throws Exception {
        when().
                get("/shipments").
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getShipment() throws Exception {
        when().
                get("shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK).
                body("id", equalTo(shipmentId));
    }

    @Test
    public void getShipment_notFound() throws Exception {
        when().
                get("/shipments/{id}", shipmentId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipment() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("senderId", (int) testHelper.createClient().getId());
        jsonObject.put("recipientId", (int) testHelper.createClient().getId());
        String expectedJson = jsonObject.toString();

        int newShipmentId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                        when().
                        post("/shipments").
                        then().
                        extract().
                        path("id");

        // check created data
        Shipment createdShipment = shipmentService.getEntityById(newShipmentId);
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateShipmentFromFile() throws Exception {
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("senderId", (int) testHelper.createClient().getId());
        jsonObject.put("recipientId", (int) testHelper.createClient().getId());
        updateShipment(jsonObject.toString());
    }

    private void updateShipment(String jsonBody) throws Exception {
        // update
        given().
                contentType("application/json;charset=UTF-8").
                body(jsonBody).
                when().
                put("/shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK);

        // check updated data
        ShipmentDto shipmentDto = shipmentService.getById(shipmentId);
        String actualJson = mapper.writeValueAsString(shipmentDto);

        JSONAssert.assertEquals(jsonBody, actualJson, false);
    }

    @Test
    public void updateShipmentConcurrent() throws Exception {
        int size = 3;
        ExecutorService executor = Executors.newFixedThreadPool(size);
        List<Callable<Boolean>> tasks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            tasks.add(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    updateShipmentFromFile();
                    return true;
                }
            });
        }
        executor.invokeAll(tasks);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    @Test
    public void deleteShipment() throws Exception {
        when().
                delete("/shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteShipment_notFound() throws Exception {
        when().
                delete("/shipments/{id}", shipmentId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    public void getParcels() throws Exception {
        when().
                get("/shipments/{id}/parcels", shipmentId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getParcel() throws Exception {
        when().
                get("/shipments/{id}/parcels/{parcel_id}", shipmentId, parcelId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getParcel_notFound() throws Exception {
        when().
                get("/shipments/{id}/parcels/{parcel_id}", shipmentId, parcelId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    public void addParcel() throws Exception {
        Parcel newParcel = new Parcel(3, 2, 0.5f, 0.2f, new BigDecimal(10.00), new BigDecimal(36.00));
        shipment.addParcel(newParcel);
        shipment.setPrice(new BigDecimal(66));
        String jsonBody = mapper.writeValueAsString(shipmentMapper.toDto(shipment));
        updateShipment(jsonBody);
    }

    @Test
    public void updateParcel() throws Exception {
        parcel.setWeight(5);
        parcel.setPrice(new BigDecimal(36));
        shipment.setPrice(new BigDecimal(36));
        String jsonBody = mapper.writeValueAsString(shipmentMapper.toDto(shipment));
        updateShipment(jsonBody);
    }

    @Test
    public void updateParcels() throws Exception {
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/edit_parcels.json");
        jsonObject.put("senderId", (int) testHelper.createClient().getId());
        jsonObject.put("recipientId", (int) testHelper.createClient().getId());
        updateShipment(jsonObject.toString());
    }

    @Test
    public void deleteParcel() throws Exception {
        shipment.removeParcel(parcel);
        shipment.setPrice(new BigDecimal(0));
        String jsonBody = mapper.writeValueAsString(shipmentMapper.toDto(shipment));
        updateShipment(jsonBody);
    }

    @Test
    public void getParcelItems() throws Exception {
        when().
                get("/shipments/{id}/parcels/{parcel_id}/items", shipmentId, parcelId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getParcelItem() throws Exception {
        when().
                get("/shipments/{id}/parcels/{parcel_id}/items/{item_id}", shipmentId, parcelId, parcelItem.getId()).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getParcelItem_notFound() throws Exception {
        when().
                get("/shipments/{id}/parcels/{parcel_id}/items/{item_id}", shipmentId, parcelId, parcelItem.getId() + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    public void addParcelItem() throws Exception {
        ParcelItem newItem = new ParcelItem("paper", 2, 5, 10.5f);
        parcel.addItem(newItem);
        String jsonBody = mapper.writeValueAsString(shipmentMapper.toDto(shipment));
        updateShipment(jsonBody);
    }

    @Test
    public void updateParcelItem() throws Exception {
        parcelItem.setPrice(12.8f);
        String jsonBody = mapper.writeValueAsString(shipmentMapper.toDto(shipment));
        updateShipment(jsonBody);
    }

    @Test
    public void deleteParcelItem() throws Exception {
        parcel.removeItem(parcelItem);
        String jsonBody = mapper.writeValueAsString(shipmentMapper.toDto(shipment));
        updateShipment(jsonBody);
    }
}
