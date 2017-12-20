package com.opinta.controller;

import java.util.List;

import com.opinta.dto.ParcelDto;
import com.opinta.dto.ParcelItemDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.service.PDFGeneratorService;
import com.opinta.service.ParcelItemService;
import com.opinta.service.ParcelService;
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
    private ShipmentService shipmentService;
    private PDFGeneratorService pdfGeneratorService;
    private ParcelService parcelService;
    private ParcelItemService parcelItemService;


    @Autowired
    public ShipmentController(ShipmentService shipmentService, PDFGeneratorService pdfGeneratorService,
                              ParcelService parcelService, ParcelItemService parcelItemService) {
        this.shipmentService = shipmentService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.parcelService = parcelService;
        this.parcelItemService = parcelItemService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<ShipmentDto> getShipments() {
        List<ShipmentDto> allShipments = shipmentService.getAll();
        for (ShipmentDto element : allShipments) {
            long shipmentId = element.getId();
            List<ParcelDto> allByShipment = parcelService.getAllByShipment(shipmentId);
            element.setParcelDtoList(allByShipment);
                for (ParcelDto elem: allByShipment) {
                    long parcelId = elem.getId();
                    List<ParcelItemDto> allByParcel = parcelItemService.getAllByParcel(parcelId);
                    elem.setParcelItemDtoList(allByParcel);
                }
        }
        return allShipments;
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getShipment(@PathVariable("id") long id) {
        ShipmentDto shipmentDto = shipmentService.getById(id);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format("No Shipment found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDto, OK);
    }

    @GetMapping("{id}/label-form")
    public ResponseEntity<?> getShipmentLabelForm(@PathVariable("id") long id) {
        byte[] data = pdfGeneratorService.generateLabel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "labelform" + id + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(data, headers, OK);
    }

    @GetMapping("{id}/postpay-form")
    public ResponseEntity<?> getShipmentPostpayForm(@PathVariable("id") long id) {
        byte[] data = pdfGeneratorService.generatePostpay(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "postpayform" + id + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
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
            return new ResponseEntity<>(format("No Shipment found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipment(@PathVariable long id) {
        if (!shipmentService.delete(id)) {
            return new ResponseEntity<>(format("No Shipment found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
