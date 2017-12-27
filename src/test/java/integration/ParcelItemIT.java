package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.Shipment;
import com.opinta.mapper.ParcelItemMapper;
import com.opinta.mapper.ParcelMapper;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ParcelItemService;
import com.opinta.service.ParcelService;
import com.opinta.service.ShipmentService;
import integration.helper.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ParcelItemIT extends BaseControllerIT {
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private ShipmentMapper shipmentMapper;
    @Autowired
    private ParcelMapper parcelMapper;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private ParcelItemService parcelItemService;
    @Autowired
    private ParcelItemMapper parcelItemMapper;
    @Autowired
    private TestHelper testHelper;

    private Shipment currentShipment;
    private Parcel currentParcel;
    private ParcelItem currentItem;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        currentShipment = testHelper.createShipment();
        currentParcel = currentShipment.getParcels().get(0);
        currentItem = currentParcel.getItems().get(0);
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteShipment(currentShipment);
    }

    @Test
    public void getParcelItems() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(parcelItemMapper.toDto(currentParcel.getItems()));

        List<ParcelItemDto> items = parcelItemService.getAllByParcel(currentParcel);
        String actualJson = objectMapper.writeValueAsString(items);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void getParcelItem() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(parcelItemMapper.toDto(currentItem));

        ParcelItemDto item = parcelItemService.getById(currentItem.getId());
        String actualJson = objectMapper.writeValueAsString(item);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void addParcelItem() throws Exception {
        ParcelItem newItem = new ParcelItem("comp", 1, 0.2f, 10000);
        currentParcel.addItem(newItem);
        String expectedJson = objectMapper.writeValueAsString(parcelItemMapper.toDto(currentParcel.getItems()));

        parcelItemService.save(parcelItemMapper.toDto(newItem));
        List<ParcelItemDto> items = parcelItemService.getAllByParcel(currentParcel);
        String actualJson = objectMapper.writeValueAsString(items);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void updateParcelItem() throws Exception {
        currentItem.setName("notepad");
        ParcelItemDto dto = parcelItemMapper.toDto(currentItem);
        String expectedJson = objectMapper.writeValueAsString(dto);

        dto = parcelItemService.update(currentItem.getId(), dto);
        String actualJson = objectMapper.writeValueAsString(dto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteParcelItem() throws Exception {
        currentParcel.removeItem(currentItem);
        String expectedJson = objectMapper.writeValueAsString(parcelItemMapper.toDto(currentParcel.getItems()));

        parcelItemService.delete(currentItem.getId());
        List<ParcelItemDto> items = parcelItemService.getAllByParcel(currentParcel);
        String actualJson = objectMapper.writeValueAsString(items);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }
}
