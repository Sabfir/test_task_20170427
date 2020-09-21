package com.opinta.service;

import com.opinta.dto.AddressDto;
import com.opinta.entity.Address;
import java.util.List;

public interface AddressService {

    List<Address> getAllEntities();

    Address getEntityById(long id);

    Address saveEntity(Address address);

    Address updateEntity(long id, Address address);

    List<AddressDto> getAll();

    AddressDto getById(long id);

    AddressDto save(AddressDto addressDto);

    AddressDto update(long id, AddressDto addressDto);

    boolean delete(long id);
}
