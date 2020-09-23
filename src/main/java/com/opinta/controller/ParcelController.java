package com.opinta.controller;

import com.opinta.entity.Parcel;
import com.opinta.service.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping
public class ParcelController {
    private final ParcelService parcelService;

    @Autowired
    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<Parcel> getParcels() {
        return parcelService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getParcel(@PathVariable("id") long id) {
        Parcel parcel = parcelService.getById(id);
        if (parcel == null) {
            return new ResponseEntity<>(format("No Shipment found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcel, OK);
    }

    @PutMapping("{id}/{shipmentId}")
    public ResponseEntity<?> updateParcels(@PathVariable long id,
                                           @PathVariable long shipmentId,
                                           @RequestBody Parcel parcel) {
        parcel = parcelService.update(id, shipmentId, parcel);
        if (parcel == null) {
            return new ResponseEntity<>(format("No Shipment found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcel, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteParcel(@PathVariable long id) {
        if (!parcelService.delete(id)) {
            return new ResponseEntity<>(format("No Shipment found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
