package com.opinta.controller;

import com.opinta.dto.ClientDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.service.ClientService;
import com.opinta.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private static final String NO_CLIENT_FOUND = "No Client found for ID %d";

    private final ClientService clientService;
    private final ShipmentService shipmentService;
    
    @Autowired
    public ClientController(ClientService clientService, ShipmentService shipmentService) {
        this.clientService = clientService;
        this.shipmentService = shipmentService;
    }
    
    @GetMapping
    @ResponseStatus(OK)
    public List<ClientDto> getAllClients() {
        return this.clientService.getAll();
    }
    
    @GetMapping("{id}")
    public ResponseEntity<?> getClient(@PathVariable("id") long id) {
        ClientDto clientDto = clientService.getById(id);
        if (clientDto == null) {
            return new ResponseEntity<>(format(NO_CLIENT_FOUND, id), NOT_FOUND);
        }
        return new ResponseEntity<>(clientDto, OK);
    }

    @GetMapping("{clientId}/shipments")
    public ResponseEntity<?> getShipmentsByClientId(@PathVariable long clientId) {
        List<ShipmentDto> shipmentDtos = shipmentService.getAllByClientId(clientId);
        if (shipmentDtos == null) {
            return new ResponseEntity<>(format("Client %d doesn't exist", clientId), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDtos, OK);
    }
    
    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody ClientDto clientDto) {
        clientDto = clientService.save(clientDto);
        if (clientDto == null) {
            return new ResponseEntity<>("New Client has not been saved", BAD_REQUEST);
        }
        return new ResponseEntity<>(clientDto, OK);
    }
    
    @PutMapping("{id}")
    public ResponseEntity<?> updateClient(@PathVariable long id, @RequestBody ClientDto clientDto) {
        clientDto = clientService.update(id, clientDto);
        if (clientDto != null) {
            return new ResponseEntity<>(clientDto, OK);
        } else {
            return new ResponseEntity<>(format(NO_CLIENT_FOUND, id), NOT_FOUND);
        }
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteClient(@PathVariable long id) {
        if (!clientService.delete(id)) {
            return new ResponseEntity<>(format(NO_CLIENT_FOUND, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
