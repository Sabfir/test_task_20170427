package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;
import com.opinta.mapper.ClientMapper;
import com.opinta.service.ClientService;
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

public class ClientControllerIT extends BaseControllerIT {
    public static final String CLIENTS = "/clients";
    public static final String ID = "id";
    public static final String CLIENTS_ID = "/clients/{id}";
    public static final String JSON_CLIENT_JSON = "json/client.json";
    public static final String COUNTERPARTY_ID = "counterpartyId";
    public static final String ADDRESS_ID = "addressId";
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
    private Client client;
    private int clientId = MIN_VALUE;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        client = testHelper.createClient();
        clientId = (int) client.getId();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteClient(client);
    }

    @Test
    public void getClients() throws Exception {
        when().
                get(CLIENTS).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getClient() throws Exception {
        when().
                get("clients/{id}", clientId).
        then().
                statusCode(SC_OK).
                body(ID, equalTo(clientId));
    }

    @Test
    public void getClient_notFound() throws Exception {
        when().
                get(CLIENTS_ID, clientId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createClient() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile(JSON_CLIENT_JSON);
        jsonObject.put(COUNTERPARTY_ID, (int) testHelper.createCounterparty().getId());
        jsonObject.put(ADDRESS_ID, (int) testHelper.createAddress().getId());
        String expectedJson = jsonObject.toString();

        int newClientId =
                given().
                        contentType(APPLICATION_JSON_CHARSET_UTF_8).
                        body(expectedJson).
                when().
                        post(CLIENTS).
                then().
                        extract().
                        path(ID);

        // check created data
        Client createdClient = clientService.getEntityById(newClientId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientMapper.toDto(createdClient));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteClient(createdClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateClient() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile(JSON_CLIENT_JSON);
        jsonObject.put(COUNTERPARTY_ID, (int) testHelper.createCounterparty().getId());
        jsonObject.put(ADDRESS_ID, (int) testHelper.createAddress().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType(APPLICATION_JSON_CHARSET_UTF_8).
                body(expectedJson).
        when().
                put(CLIENTS_ID, clientId).
        then().
                statusCode(SC_OK);

        // check updated data
        ClientDto clientDto = clientMapper.toDto(clientService.getEntityById(clientId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteClient() throws Exception {
        when().
                delete(CLIENTS_ID, clientId).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteClient_notFound() throws Exception {
        when().
                delete(CLIENTS_ID, clientId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
