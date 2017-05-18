package com.opinta.dao;

import com.opinta.entity.Address;

import java.util.List;

public interface AddressDao {

    List<Address> getAll();

    Address getById(long id);

    Address save(Address address);

    void update(Address address);

    void delete(Address address);
}
