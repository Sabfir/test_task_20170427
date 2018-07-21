package com.opinta.controller;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.service.BarcodeInnerNumberService;
import com.opinta.service.PostcodePoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/postcodes")
public class PostcodePoolController {
    private static final String NO_POSTCODEPOOL_FOUND = "No PostcodePool found for ID %d";
    private static final String POSTCODEPOOL_NOT_EXIST = "PostcodePool %d doesn't exist";
    private static final String NO_BARCODEINNERNUMBER_FOUND = "No barcodeInnerNumber found for ID %d";

    private PostcodePoolService postcodePoolService;
    private BarcodeInnerNumberService barcodeInnerNumberService;

    @Autowired
    public PostcodePoolController(PostcodePoolService postcodePoolService,
                                  BarcodeInnerNumberService barcodeInnerNumberService) {
        this.postcodePoolService = postcodePoolService;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<PostcodePoolDto> getPostcodePools() {
        return postcodePoolService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPostcodePool(@PathVariable("id") long id) {
        PostcodePoolDto postcodePoolDto = postcodePoolService.getById(id);
        if (postcodePoolDto == null) {
            return new ResponseEntity<>(format(NO_POSTCODEPOOL_FOUND, id), NOT_FOUND);
        }
        return new ResponseEntity<>(postcodePoolDto, OK);
    }

    @PostMapping
    @ResponseStatus(OK)
    public PostcodePoolDto createPostcodePool(@RequestBody PostcodePoolDto postcodePoolDto) {
        return postcodePoolService.save(postcodePoolDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatePostcodePool(@PathVariable long id, @RequestBody PostcodePoolDto postcodePoolDto) {
        postcodePoolDto = postcodePoolService.update(id, postcodePoolDto);
        if (postcodePoolDto == null) {
            return new ResponseEntity<>(format(NO_POSTCODEPOOL_FOUND, id), NOT_FOUND);
        }
        return new ResponseEntity<>(postcodePoolDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostcodePool(@PathVariable long id) {
        if (!postcodePoolService.delete(id)) {
            return new ResponseEntity<>(format(NO_POSTCODEPOOL_FOUND, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }

    @GetMapping("{postcodeId}/inner-numbers")
    public ResponseEntity<?> getBarcodeInnerNumbers(@PathVariable long postcodeId) {
        List<BarcodeInnerNumberDto> barcodeInnerNumberDtos = barcodeInnerNumberService.getAll(postcodeId);
        if (barcodeInnerNumberDtos == null) {
            return new ResponseEntity<>(format(POSTCODEPOOL_NOT_EXIST, postcodeId), NOT_FOUND);
        }
        return new ResponseEntity<>(barcodeInnerNumberDtos, OK);
    }

    @GetMapping("inner-numbers/{id}")
    public ResponseEntity<?> getBarcodeInnerNumber(@PathVariable("id") long id) {
        BarcodeInnerNumberDto barcodeInnerNumberDto = barcodeInnerNumberService.getById(id);
        if (barcodeInnerNumberDto == null) {
            return new ResponseEntity<>(format(NO_BARCODEINNERNUMBER_FOUND, id), NOT_FOUND);
        }
        return new ResponseEntity<>(barcodeInnerNumberDto, OK);
    }

    @PostMapping("{postcodeId}/inner-numbers")
    @ResponseStatus(OK)
    public ResponseEntity<?> createBarcodeInnerNumber(@PathVariable("postcodeId") long postcodeId,
                                                      @RequestBody BarcodeInnerNumberDto barcodeInnerNumberDto) {
        barcodeInnerNumberDto =
                barcodeInnerNumberService.save(postcodeId, barcodeInnerNumberDto);
        if (barcodeInnerNumberDto == null) {
            return new ResponseEntity<>(format(POSTCODEPOOL_NOT_EXIST, postcodeId), NOT_FOUND);
        }
        return new ResponseEntity<>(barcodeInnerNumberDto, OK);
    }

    @DeleteMapping("inner-numbers/{id}")
    public ResponseEntity<?> deleteBarcodeInnerNumber(@PathVariable long id) {
        if (!barcodeInnerNumberService.delete(id)) {
            return new ResponseEntity<>(format(NO_BARCODEINNERNUMBER_FOUND, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
