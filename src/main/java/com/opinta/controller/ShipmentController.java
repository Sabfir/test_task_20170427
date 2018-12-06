package com.opinta.controller;

import java.util.List;

import com.opinta.dto.ParcelDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.service.PDFGeneratorService;
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
    private final ShipmentService shipmentService;
    private final PDFGeneratorService pdfGeneratorService;
    private final ParcelService parcelService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService, PDFGeneratorService pdfGeneratorService,
                                ParcelService parcelService) {
        this.shipmentService = shipmentService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.parcelService = parcelService;
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

    @GetMapping("{shipmentId}/parcels")
    public ResponseEntity<?> getParcels(@PathVariable long shipmentId) {
        List<ParcelDto> parcelDtos = parcelService.getAllByShipmentId(shipmentId);
        if (parcelDtos == null) {
            return new ResponseEntity<>(format("Shipment %d doesn't exist", shipmentId), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDtos, OK);
    }

    @GetMapping("parcels/{id}")
    public ResponseEntity<?> getParcel(@PathVariable("id") long id) {
        ParcelDto parcelDto = parcelService.getById(id);
        if (parcelDto == null) {
            return new ResponseEntity<>(format("No parcel found for id %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDto, OK);
    }

    @PostMapping("{shipmentId}/parcels")
    public ResponseEntity<?> createParcel(@PathVariable("shipmentId")long shipmentId,
                                          @RequestBody ParcelDto parcelDto) {
        parcelDto = parcelService.save(shipmentId, parcelDto);
        if (parcelDto == null) {
            return new ResponseEntity<>(format("Shipment %d doesn't exist", shipmentId), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDto, OK);
    }

    public ResponseEntity<?> deleteParcel(@PathVariable long id) {
        if (!parcelService.delete(id)) {
            return new ResponseEntity<>(format("No parcel found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
