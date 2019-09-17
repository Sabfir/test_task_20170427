package com.opinta.dao;

import com.opinta.entity.BarcodeInnerNumber;

import java.util.List;

public interface BarcodeInnerNumberDao {
    
    List<BarcodeInnerNumber> getAll(long postcodeId);
    
    BarcodeInnerNumber getById(long id);
    
    BarcodeInnerNumber save(BarcodeInnerNumber barcodeInnerNumber);
    
    void update(BarcodeInnerNumber barcodeInnerNumber);
    
    void delete(BarcodeInnerNumber barcodeInnerNumber);
}
