package com.opinta.controller;

import com.opinta.dto.AddressDto;
import com.opinta.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<AddressDto> getAddresses() {
        return addressService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAddress(@PathVariable("id") long id) {
        AddressDto addressDto = addressService.getById(id);
        if (addressDto == null) {
            return new ResponseEntity<>(format("No Address found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(addressDto, OK);
    }

    @PostMapping
    @ResponseStatus(OK)
    public ResponseEntity<?> createAddress(@RequestBody AddressDto addressDto) {
        addressDto = addressService.save(addressDto);
        if (addressDto == null) {
            return new ResponseEntity<>("Failed to create new Address using given data.", BAD_REQUEST);
        }
        return new ResponseEntity<>(addressDto, OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateAddress(@PathVariable long id, @RequestBody AddressDto addressDto) {
        addressDto = addressService.update(id, addressDto);
        if (addressDto == null) {
            return new ResponseEntity<>(format("No Address found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(addressDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable long id) {
        if (!addressService.delete(id)) {
            return new ResponseEntity<>(format("No Address found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
