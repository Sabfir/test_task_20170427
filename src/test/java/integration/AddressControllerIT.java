package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.entity.Address;
import com.opinta.service.AddressService;
import integration.helper.TestHelper;
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

import static org.hamcrest.CoreMatchers.equalTo;

public class AddressControllerIT extends BaseControllerIT {
    private static final String ADDRESSES_ID = "/addresses/{id}";
    private static final String ID = "id";
    private static final String ADDRESSES = "/addresses";
    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";
    private static final String JSON_ADDRESS = "json/address.json";
    private int addressId = MIN_VALUE;
    @Autowired
    private AddressService addressService;
    @Autowired
    private TestHelper testHelper;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        addressId = (int) testHelper.createAddress().getId();
    }
    
    @After
    public void tearDown() {
        addressService.delete(addressId);
    }
    
    @Test
    public void getAddresses() throws Exception {
        when().
                get(ADDRESSES).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getAddress() throws Exception {
        when().
                get(ADDRESSES_ID, addressId).
        then().
                statusCode(SC_OK).
                body(ID, equalTo(addressId));
    }

    @Test
    public void getAddress_notFound() throws Exception {
        when().
                get(ADDRESSES_ID, addressId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    public void createAddress() throws Exception {
        // create
        String expectedJson = testHelper.getJsonFromFile(JSON_ADDRESS);

        int newAddressId =
                given().
                        contentType(APPLICATION_JSON).
                        body(expectedJson).
                when().
                        post(ADDRESSES).
                then().
                        extract().
                        path(ID);

        // check created data
        Address address = addressService.getEntityById(newAddressId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(address);

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        addressService.delete(newAddressId);
    }

    @Test
    public void updateAddress() throws Exception {
        // update data
        String expectedJson = testHelper.getJsonFromFile(JSON_ADDRESS);

        given().
                contentType(APPLICATION_JSON).
                body(expectedJson).
        when().
                put(ADDRESSES_ID, addressId).
        then().
                statusCode(SC_OK);

        // check if updated
        Address address = addressService.getEntityById(addressId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(address);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteAddress() throws Exception {
        when()
                .delete(ADDRESSES_ID, addressId).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteAddress_notFound() throws Exception {
        when()
                .delete(ADDRESSES_ID, addressId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
