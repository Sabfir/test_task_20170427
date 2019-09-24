package com.opinta.service;

import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PDFGeneratorServiceTest {
    public static final String MONASTIRISKA = "Monastiriska";
    public static final String KIEV = "Kiev";
    public static final String FOP_IVANOV = "FOP Ivanov";
    public static final String PETROV_PP = "Petrov PP";
    public static final String VAL_TWO_AND_HALF = "2.5";
    public static final String VAL_TWELVE_AND_TWENTY_FIVE = "12.5";
    public static final String VAL_FIFTIN_AND_TWENTY_FIVE = "15.25";
    public static final String SENDER_NAME = "senderName";
    public static final String EXPECTED_SENDER_NAME_FORM_TO_CONTAIN_FOP_IVANOV =
            "Expected senderName form to contain FOP Ivanov";
    public static final String SENDER_ADDRESS = "senderAddress";
    public static final String EXPECTED_SENDER_ADDRESS_FORM_TO_CONTAIN_SADOVA_ST_51_MONASTIRISKA_00001 =
            "Expected senderAddress form to contain Sadova st., 51, Monastiriska\n00001";
    public static final String SADOVA_ST_51_MONASTIRISKA_00001 = "Sadova st., 51, Monastiriska\n00001";
    public static final String RECIPIENT_NAME = "recipientName";
    public static final String EXPECTED_SENDER_NAME_FORM_TO_CONTAIN_PETROV_PP =
            "Expected senderName form to contain Petrov PP";
    public static final String RECIPIENT_ADDRESS = "recipientAddress";
    public static final String EXPECTED_RECIPIENT_ADDRESS_FORM_TO_CONTAIN_KHRESCHATIK_ST_121_KIEV_00002 =
            "Expected recipientAddress form to contain Khreschatik st., 121, Kiev\n00002";
    public static final String KHRESCHATIK_ST_121_KIEV_00002 = "Khreschatik st., 121, Kiev\n00002";
    @Mock
    private ShipmentService shipmentService;

    private PDFGeneratorService pdfGeneratorService;
    private ParcelItem parcelItem;
    private Parcel parcel;
    private Shipment shipment;

    @Before
    public void setUp() throws Exception {
        pdfGeneratorService = new PDFGeneratorServiceImpl(shipmentService);

        Address senderAddress = new Address("00001", "Ternopil", MONASTIRISKA,
                MONASTIRISKA, "Sadova", "51", "");
        Address recipientAddress = new Address("00002", KIEV, "", KIEV, "Khreschatik", "121", "37");
        Counterparty counterparty = new Counterparty("Modna kasta",
                new PostcodePool("00003", false));
        Client sender = new Client(FOP_IVANOV, "001", senderAddress, counterparty);
        Client recipient = new Client(PETROV_PP, "002", recipientAddress, counterparty);
        parcelItem = new ParcelItem("Ivan", 2, 1, new BigDecimal(VAL_TWO_AND_HALF));
        parcel = new Parcel(1f, 1f, new BigDecimal(VAL_TWO_AND_HALF), new BigDecimal(VAL_TWELVE_AND_TWENTY_FIVE));
        parcel.setParcelItems(Collections.singletonList(parcelItem));
        shipment = new Shipment(sender, recipient, DeliveryType.W2W, new BigDecimal(VAL_FIFTIN_AND_TWENTY_FIVE));
        shipment.setPrice(new BigDecimal(VAL_TWO_AND_HALF));
        shipment.setParcelList(Collections.singletonList(parcel));
    }

    @Test
    public void generateLabel_and_generatePostpay_ShouldReturnNotEmptyFile() {
        when(shipmentService.getEntityById(1L)).thenReturn(shipment);
        assertNotEquals("PDFGenerator returned an empty label",
                pdfGeneratorService.generateLabel(1L).length, 0);
        assertNotEquals("PDFGenerator returned an empty postpay form",
                pdfGeneratorService.generateLabel(1L).length, 0);
        verify(shipmentService, atLeast(2)).getEntityById(1L);
    }

    @Test
    public void generateLabel_ShouldReturnValidAcroForms() throws Exception {
        when(shipmentService.getEntityById(1L)).thenReturn(shipment);

        byte[] labelForm = pdfGeneratorService.generateLabel(1L);

        PDAcroForm acroForm = getAcroFormFromPdfFile(labelForm);

        PDTextField field = (PDTextField) acroForm.getField(SENDER_NAME);
        assertEquals(EXPECTED_SENDER_NAME_FORM_TO_CONTAIN_FOP_IVANOV,
                field.getValue(), FOP_IVANOV);

        field = (PDTextField) acroForm.getField(SENDER_ADDRESS);
        assertEquals(EXPECTED_SENDER_ADDRESS_FORM_TO_CONTAIN_SADOVA_ST_51_MONASTIRISKA_00001,
                field.getValue(), SADOVA_ST_51_MONASTIRISKA_00001);

        field = (PDTextField) acroForm.getField(RECIPIENT_NAME);
        assertEquals(EXPECTED_SENDER_NAME_FORM_TO_CONTAIN_PETROV_PP, field.getValue(), PETROV_PP);

        field = (PDTextField) acroForm.getField(RECIPIENT_ADDRESS);
        assertEquals(EXPECTED_RECIPIENT_ADDRESS_FORM_TO_CONTAIN_KHRESCHATIK_ST_121_KIEV_00002,
                field.getValue(), KHRESCHATIK_ST_121_KIEV_00002);

        field = (PDTextField) acroForm.getField("mass");
        assertEquals("Expected mass to be 1.0", field.getValue(), "1.0");

        field = (PDTextField) acroForm.getField("value");
        assertEquals("Expected value to be 12.5", field.getValue(), VAL_TWELVE_AND_TWENTY_FIVE);

        field = (PDTextField) acroForm.getField("sendingCost");
        assertEquals("Expected sendingCost to be 2.5", field.getValue(), VAL_TWO_AND_HALF);

        field = (PDTextField) acroForm.getField("postPrice");
        assertEquals("Expected postPrice to be 15.25", field.getValue(), VAL_FIFTIN_AND_TWENTY_FIVE);

        field = (PDTextField) acroForm.getField("totalCost");
        assertEquals("Expected totalCost to be 15", field.getValue(), VAL_FIFTIN_AND_TWENTY_FIVE);

        verify(shipmentService).getEntityById(1L);
    }

    @Test
    public void generatePostpay_ShouldReturnValidAcroForms() throws Exception {
        when(shipmentService.getEntityById(1L)).thenReturn(shipment);

        byte[] postpayForm = pdfGeneratorService.generatePostpay(1L);

        PDAcroForm acroForm = getAcroFormFromPdfFile(postpayForm);

        PDTextField field = (PDTextField) acroForm.getField(SENDER_NAME);
        assertEquals(EXPECTED_SENDER_NAME_FORM_TO_CONTAIN_FOP_IVANOV,
                field.getValue(), FOP_IVANOV);

        field = (PDTextField) acroForm.getField(SENDER_ADDRESS);
        assertEquals(EXPECTED_SENDER_ADDRESS_FORM_TO_CONTAIN_SADOVA_ST_51_MONASTIRISKA_00001,
                field.getValue(), SADOVA_ST_51_MONASTIRISKA_00001);

        field = (PDTextField) acroForm.getField(RECIPIENT_NAME);
        assertEquals(EXPECTED_SENDER_NAME_FORM_TO_CONTAIN_PETROV_PP, field.getValue(), PETROV_PP);

        field = (PDTextField) acroForm.getField(RECIPIENT_ADDRESS);
        assertEquals(EXPECTED_RECIPIENT_ADDRESS_FORM_TO_CONTAIN_KHRESCHATIK_ST_121_KIEV_00002,
                field.getValue(), KHRESCHATIK_ST_121_KIEV_00002);

        field = (PDTextField) acroForm.getField("priceHryvnas");
        assertEquals("Expected priceHryvnas to be 15", field.getValue(), "15");

        field = (PDTextField) acroForm.getField("priceKopiyky");
        assertEquals("Expected priceKopiyky to be 25", field.getValue(), "25");

        verify(shipmentService).getEntityById(1L);
    }

    private PDAcroForm getAcroFormFromPdfFile(byte[] postpayForm) throws IOException {
        return PDDocument
                .load(postpayForm)
                .getDocumentCatalog()
                .getAcroForm();
    }
}
