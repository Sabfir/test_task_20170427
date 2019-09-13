package com.opinta.controller;

import java.util.List;

import com.opinta.dto.ShipmentDto;
import com.opinta.service.PDFGeneratorService;
import com.opinta.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {
    private static final String SHIPMENT_ERROR_MESSAGE = "No Shipment found for ID %d";
    private static final String APPLICATION_PDF = "application/pdf";
    private static final String PDF_SUFFIX = ".pdf";
    private static final String CACHE_CONTROL_VALUE = "must-revalidate, post-check=0, pre-check=0";
    private ShipmentService shipmentService;
    private PDFGeneratorService pdfGeneratorService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService, PDFGeneratorService pdfGeneratorService) {
        this.shipmentService = shipmentService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<ShipmentDto> getShipments() {
        return shipmentService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getShipment(@PathVariable("id") long id) {
        ShipmentDto shipmentDto = shipmentService.getById(id);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format(SHIPMENT_ERROR_MESSAGE, id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDto, OK);
    }

    @GetMapping("{id}/label-form")
    public ResponseEntity<?> getShipmentLabelForm(@PathVariable("id") long id) {
        byte[] data = pdfGeneratorService.generateLabel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(APPLICATION_PDF));
        String filename = "labelform" + id + PDF_SUFFIX;
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl(CACHE_CONTROL_VALUE);
        return new ResponseEntity<>(data, headers, OK);
    }

    @GetMapping("{id}/postpay-form")
    public ResponseEntity<?> getShipmentPostpayForm(@PathVariable("id") long id) {
        byte[] data = pdfGeneratorService.generatePostpay(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(APPLICATION_PDF));
        String filename = "postpayform" + id + PDF_SUFFIX;
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl(CACHE_CONTROL_VALUE);
        return new ResponseEntity<>(data, headers, OK);
    }

    @PostMapping
    @ResponseStatus(OK)
    public ShipmentDto createShipment(@RequestBody ShipmentDto shipmentDto) {
        return shipmentService.save(shipmentDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateShipment(@PathVariable long id, @RequestBody ShipmentDto shipmentDto) {
        shipmentDto = shipmentService.update(id, shipmentDto);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format(SHIPMENT_ERROR_MESSAGE, id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipment(@PathVariable long id) {
        if (!shipmentService.delete(id)) {
            return new ResponseEntity<>(format(SHIPMENT_ERROR_MESSAGE, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}