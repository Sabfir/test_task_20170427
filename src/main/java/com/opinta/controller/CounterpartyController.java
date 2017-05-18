package com.opinta.controller;

import com.opinta.dto.ClientDto;
import com.opinta.dto.CounterpartyDto;
import com.opinta.service.ClientService;
import com.opinta.service.CounterpartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("/counterparties")
public class CounterpartyController {
    private final CounterpartyService counterpartyService;
    private final ClientService clientService;

    @Autowired
    public CounterpartyController(CounterpartyService counterpartyService, ClientService clientService) {
        this.counterpartyService = counterpartyService;
        this.clientService = clientService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<CounterpartyDto> getAllPostOffices() {
        return counterpartyService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPostOffice(@PathVariable("id") long id) {
        CounterpartyDto counterpartyDto = counterpartyService.getById(id);
        if (counterpartyDto == null) {
            return new ResponseEntity<>(format("No Counterparty found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }

    @GetMapping("{counterpartyId}/clients")
    public ResponseEntity<?> getClientsByCounterpartyId(@PathVariable long counterpartyId) {
        List<ClientDto> clientDtos = clientService.getAllByCounterpartyId(counterpartyId);
        if (clientDtos == null) {
            return new ResponseEntity<>(format("No Counterparty found for ID %d", counterpartyId), NOT_FOUND);
        }
        return new ResponseEntity<>(clientDtos, OK);
    }

    @PostMapping
    public ResponseEntity<?> createCounterparty(@RequestBody CounterpartyDto counterpartyDto) {
        counterpartyDto = counterpartyService.save(counterpartyDto);
        if (counterpartyDto == null) {
            return new ResponseEntity<>("New Counterparty has not been saved", BAD_REQUEST);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatePostOfficeById(@PathVariable("id") long id,
                                                  @RequestBody CounterpartyDto counterpartyDto) {
        counterpartyDto = counterpartyService.update(id, counterpartyDto);
        if (counterpartyDto == null) {
            return new ResponseEntity<>(format("No Counterparty found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostOfficeById(@PathVariable("id") long id) {
        if (!this.counterpartyService.delete(id)) {
            return new ResponseEntity<>(format("No Counterparty found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
