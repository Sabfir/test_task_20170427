package com.opinta.service;

import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.Parcel;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PDFGeneratorServiceTest {
    private static final String MONASTIRISKA = "Monastiriska";
    private static final String KIEV = "Kiev";
    private static final String FOP_IVANOV = "FOP Ivanov";
    private static final String PETROV_PP = "Petrov PP";
    private static final String FIFTEEN_AND_ONE_FOURTH = "15.25";
    private static final String TWELVE_AND_HALF = "12.5";
    private static final String TWO_AND_HALF = "2.5";
    private static final String SENDER_NAME = "senderName";
    private static final String FOP_IVANOV_ERROR_MESSAGE = "Expected senderName form to contain FOP Ivanov";
    private static final String SENDER_ADDRESS = "senderAddress";
    private static final String SENDER_ADDRESS_ERROR_MESSAGE =
            "Expected senderAddress form to contain Sadova st., 51, Monastiriska\n00001";
    private static final String SENDER_ADDRESS_VALUE = "Sadova st., 51, Monastiriska\n00001";
    private static final String RECIPIENT_NAME = "recipientName";
    private static final String PETROV_PP_ERROR_MESSAGE = "Expected senderName form to contain Petrov PP";
    private static final String RECIPIENT_ADDRESS = "recipientAddress";
    private static final String RECIPIENT_ADDRESS_ERROR_MESSAGE =
            "Expected recipientAddress form to contain Khreschatik st., 121, Kiev\n00002";
    private static final String RECIPIENT_ADDRESS_VALUE = "Khreschatik st., 121, Kiev\n00002";
    @Mock
    private ShipmentService shipmentService;

    private PDFGeneratorService pdfGeneratorService;
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
        shipment = new Shipment(sender, recipient, DeliveryType.W2W, new BigDecimal(FIFTEEN_AND_ONE_FOURTH));
        Parcel parcel = new Parcel(1F, 1F, new BigDecimal(TWELVE_AND_HALF), new BigDecimal(TWO_AND_HALF));
        shipment.getParcels().add(parcel);
        shipment.setPrice(parcel.getPrice());
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
        assertEquals(FOP_IVANOV_ERROR_MESSAGE,
                field.getValue(), FOP_IVANOV);

        field = (PDTextField) acroForm.getField(SENDER_ADDRESS);
        assertEquals(SENDER_ADDRESS_ERROR_MESSAGE,
                field.getValue(), SENDER_ADDRESS_VALUE);

        field = (PDTextField) acroForm.getField(RECIPIENT_NAME);
        assertEquals(PETROV_PP_ERROR_MESSAGE, field.getValue(), PETROV_PP);

        field = (PDTextField) acroForm.getField(RECIPIENT_ADDRESS);
        assertEquals(RECIPIENT_ADDRESS_ERROR_MESSAGE,
                field.getValue(), RECIPIENT_ADDRESS_VALUE);

        field = (PDTextField) acroForm.getField("mass");
        assertEquals("Expected mass to be 1.0", field.getValue(), "1.0");

        field = (PDTextField) acroForm.getField("value");
        assertEquals("Expected value to be 12.5", field.getValue(), TWELVE_AND_HALF);

        field = (PDTextField) acroForm.getField("sendingCost");
        assertEquals("Expected sendingCost to be 2.5", field.getValue(), TWO_AND_HALF);

        field = (PDTextField) acroForm.getField("postPrice");
        assertEquals("Expected postPrice to be 15.25", field.getValue(), FIFTEEN_AND_ONE_FOURTH);

        field = (PDTextField) acroForm.getField("totalCost");
        assertEquals("Expected totalCost to be 15", field.getValue(), FIFTEEN_AND_ONE_FOURTH);

        verify(shipmentService).getEntityById(1L);
    }

    @Test
    public void generatePostpay_ShouldReturnValidAcroForms() throws Exception {
        when(shipmentService.getEntityById(1L)).thenReturn(shipment);

        byte[] postpayForm = pdfGeneratorService.generatePostpay(1L);

        PDAcroForm acroForm = getAcroFormFromPdfFile(postpayForm);

        PDTextField field = (PDTextField) acroForm.getField(SENDER_NAME);
        assertEquals(FOP_IVANOV_ERROR_MESSAGE,
                field.getValue(), FOP_IVANOV);

        field = (PDTextField) acroForm.getField(SENDER_ADDRESS);
        assertEquals(SENDER_ADDRESS_ERROR_MESSAGE,
                field.getValue(), SENDER_ADDRESS_VALUE);

        field = (PDTextField) acroForm.getField(RECIPIENT_NAME);
        assertEquals(PETROV_PP_ERROR_MESSAGE, field.getValue(), PETROV_PP);

        field = (PDTextField) acroForm.getField(RECIPIENT_ADDRESS);
        assertEquals(RECIPIENT_ADDRESS_ERROR_MESSAGE,
                field.getValue(), RECIPIENT_ADDRESS_VALUE);

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
