package com.opinta.controller;

import com.opinta.dto.ClientDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.service.ClientService;
import com.opinta.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private static final String NO_CLIENT_FOUND_FOR_ID = "No Client found for ID %d";
    private final ClientService clientService;
    private final ShipmentService shipmentService;

    @Autowired
    public ClientController(ClientService clientService, ShipmentService shipmentService) {
        this.clientService = clientService;
        this.shipmentService = shipmentService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientDto> getAllClients() {
        return this.clientService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getClient(@PathVariable("id") long id) {
        ClientDto clientDto = clientService.getById(id);
        if (clientDto == null) {
            return new ResponseEntity<>(format(NO_CLIENT_FOUND_FOR_ID, id), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(clientDto, HttpStatus.OK);
    }

    @GetMapping("{clientId}/shipments")
    public ResponseEntity<?> getShipmentsByClientId(@PathVariable long clientId) {
        List<ShipmentDto> shipmentDtos = shipmentService.getAllByClientId(clientId);
        if (shipmentDtos == null) {
            return new ResponseEntity<>(format("Client %d doesn't exist", clientId), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody ClientDto clientDto) {
        clientDto = clientService.save(clientDto);
        if (clientDto == null) {
            return new ResponseEntity<>("New Client has not been saved", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(clientDto, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateClient(@PathVariable long id, @RequestBody ClientDto clientDto) {
        clientDto = clientService.update(id, clientDto);
        if (clientDto != null) {
            return new ResponseEntity<>(clientDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(format(NO_CLIENT_FOUND_FOR_ID, id), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteClient(@PathVariable long id) {
        if (!clientService.delete(id)) {
            return new ResponseEntity<>(format(NO_CLIENT_FOUND_FOR_ID, id), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
