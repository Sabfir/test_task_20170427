package com.opinta.dao;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import java.util.List;


public interface CounterpartyDao {
    
    List<Counterparty> getAll();
    
    Counterparty getById(long id);

    List<Counterparty> getByPostcodePool(PostcodePool postcodePool);

    Counterparty save(Counterparty counterparty);
    
    void update(Counterparty counterparty);
    
    void delete(Counterparty counterparty);
}
