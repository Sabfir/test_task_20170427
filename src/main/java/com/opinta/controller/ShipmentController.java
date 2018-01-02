package com.opinta.controller;

import java.util.List;
import java.util.Optional;

import com.opinta.dto.ParcelDto;
import com.opinta.dto.ParcelItemDto;
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
    private static final String NO_SHIPMENT_FOUND_FOR_ID = "No Shipment found for ID %d";
    private static final String NO_PARCEL_FOUND_FOR_ID = "No Parcel found for ID %d";
    private static final String NO_PARCEL_ITEM_FOUND_FOR_ID = "No Parcel item found for ID %d";
    private static final String APP_PDF = "application/pdf";
    private static final String PDF_EXT = ".pdf";
    private static final String CACHE_CONTROL = "must-revalidate, post-check=0, pre-check=0";

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
            return new ResponseEntity<>(format(NO_SHIPMENT_FOUND_FOR_ID, id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDto, OK);
    }

    @GetMapping("{id}/label-form")
    public ResponseEntity<?> getShipmentLabelForm(@PathVariable("id") long id) {
        byte[] data = pdfGeneratorService.generateLabel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(APP_PDF));
        String filename = "labelform" + id + PDF_EXT;
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl(CACHE_CONTROL);
        return new ResponseEntity<>(data, headers, OK);
    }

    @GetMapping("{id}/postpay-form")
    public ResponseEntity<?> getShipmentPostpayForm(@PathVariable("id") long id) {
        byte[] data = pdfGeneratorService.generatePostpay(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(APP_PDF));
        String filename = "postpayform" + id + PDF_EXT;
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl(CACHE_CONTROL);
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
            return new ResponseEntity<>(format(NO_SHIPMENT_FOUND_FOR_ID, id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipment(@PathVariable long id) {
        if (!shipmentService.delete(id)) {
            return new ResponseEntity<>(format(NO_SHIPMENT_FOUND_FOR_ID, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }

    @GetMapping("{id}/parcels")
    public ResponseEntity<?> getParcels(@PathVariable long id) {
        ShipmentDto shipmentDto = shipmentService.getById(id);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format(NO_SHIPMENT_FOUND_FOR_ID, id), NOT_FOUND);
        }
        List<ParcelDto> parcels = shipmentDto.getParcels();
        return new ResponseEntity<>(parcels, OK);
    }

    @GetMapping("{id}/parcels/{parcel_id}")
    public ResponseEntity<?> getParcel(@PathVariable("id") long shipmentId, @PathVariable("parcel_id") long parcelId) {
        ShipmentDto shipmentDto = shipmentService.getById(shipmentId);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format(NO_SHIPMENT_FOUND_FOR_ID, shipmentId), NOT_FOUND);
        }
        Optional<ParcelDto> parcelDto = shipmentDto.getParcels().stream().filter(p -> p.getId() == parcelId).findAny();
        if (!parcelDto.isPresent()) {
            return new ResponseEntity<>(format(NO_PARCEL_FOUND_FOR_ID, parcelId), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDto.get(), OK);
    }

    @GetMapping("{id}/parcels/{parcel_id}/items")
    public ResponseEntity<?> getParcelItems(@PathVariable("id") long shipmentId,
                                            @PathVariable("parcel_id") long parcelId) {
        ShipmentDto shipmentDto = shipmentService.getById(shipmentId);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format(NO_SHIPMENT_FOUND_FOR_ID, shipmentId), NOT_FOUND);
        }
        Optional<ParcelDto> parcelDto = shipmentDto.getParcels().stream().filter(p -> p.getId() == parcelId).findAny();
        if (!parcelDto.isPresent()) {
            return new ResponseEntity<>(format(NO_PARCEL_FOUND_FOR_ID, parcelId), NOT_FOUND);
        }
        List<ParcelItemDto> items = parcelDto.get().getItems();
        return new ResponseEntity<>(items, OK);
    }

    @GetMapping("{id}/parcels/{parcel_id}/items/{item_id}")
    public ResponseEntity<?> getParcelItem(@PathVariable("id") long shipmentId,
                                            @PathVariable("parcel_id") long parcelId,
                                            @PathVariable("item_id") long itemId) {
        ShipmentDto shipmentDto = shipmentService.getById(shipmentId);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format(NO_SHIPMENT_FOUND_FOR_ID, shipmentId), NOT_FOUND);
        }
        Optional<ParcelDto> parcelDto = shipmentDto.getParcels().stream().filter(p -> p.getId() == parcelId).findAny();
        if (!parcelDto.isPresent()) {
            return new ResponseEntity<>(format(NO_PARCEL_FOUND_FOR_ID, parcelId), NOT_FOUND);
        }
        List<ParcelItemDto> items = parcelDto.get().getItems();
        Optional<ParcelItemDto> itemDto = items.stream().filter(i -> i.getId() == itemId).findAny();
        if (!itemDto.isPresent()) {
            return new ResponseEntity<>(format(NO_PARCEL_ITEM_FOUND_FOR_ID, itemId), NOT_FOUND);
        }
        return new ResponseEntity<>(itemDto.get(), OK);
    }
}
