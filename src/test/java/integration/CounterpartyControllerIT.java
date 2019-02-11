package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.CounterpartyDto;
import com.opinta.entity.Counterparty;
import com.opinta.mapper.CounterpartyMapper;
import com.opinta.service.CounterpartyService;
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
import static org.hamcrest.CoreMatchers.equalTo;

public class CounterpartyControllerIT extends BaseControllerIT {
    private static final String ID = "id";
    private static final String COUNTERPARTIES = "/counterparties";
    private static final String COUNTERPARTIES_ID = "/counterparties/{id}";
    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";
    private static final String POSTCODEPOOL_ID = "postcodePoolId";
    private static final String JSON_COUNTERPARTY = "json/counterparty.json";

    private Counterparty counterparty;
    private int counterpartyId = MIN_VALUE;

    @Autowired
    private CounterpartyService counterpartyService;
    @Autowired
    private CounterpartyMapper counterpartyMapper;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        counterparty = testHelper.createCounterparty();
        counterpartyId = (int) counterparty.getId();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteCounterpartyWithPostcodePool(counterparty);
    }

    @Test
    public void getCounterparties() throws Exception {
        when().
                get(COUNTERPARTIES).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getCounterparty() throws Exception {
        when().
                get(COUNTERPARTIES_ID, counterpartyId).
        then().
                statusCode(SC_OK).
                body(ID, equalTo(counterpartyId));
    }

    @Test
    public void getCounterparty_notFound() throws Exception {
        when().
                get(COUNTERPARTIES_ID, counterpartyId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createCounterparty() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile(JSON_COUNTERPARTY);
        jsonObject.put(POSTCODEPOOL_ID, (int) testHelper.createPostcodePool().getId());
        String expectedJson = jsonObject.toString();

        int newCounterpartyId =
                given().
                        contentType(APPLICATION_JSON).
                        body(expectedJson).
                when().
                        post(COUNTERPARTIES).
                then().
                        extract().
                        path(ID);

        // check created data
        Counterparty createdCounterparty = counterpartyService.getEntityById(newCounterpartyId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(counterpartyMapper.toDto(createdCounterparty));
        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteCounterpartyWithPostcodePool(createdCounterparty);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateCounterparty() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile(JSON_COUNTERPARTY);
        jsonObject.put(POSTCODEPOOL_ID, (int) testHelper.createPostcodePool().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType(APPLICATION_JSON).
                body(expectedJson).
        when().
                put(COUNTERPARTIES_ID, counterpartyId).
        then().
                statusCode(SC_OK);

        // check updated data
        CounterpartyDto counterpartyDto = counterpartyMapper
                .toDto(counterpartyService.getEntityById(counterpartyId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(counterpartyDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteCounterparty() throws Exception {
        when().
                delete(COUNTERPARTIES_ID, counterpartyId).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteCounterparty_notFound() throws Exception {
        when().
                delete(COUNTERPARTIES_ID, counterpartyId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
