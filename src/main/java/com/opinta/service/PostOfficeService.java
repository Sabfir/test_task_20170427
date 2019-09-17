package com.opinta.service;

import com.opinta.dto.PostOfficeDto;
import com.opinta.entity.PostOffice;

import java.util.List;

public interface PostOfficeService {

    List<PostOffice> getAllEntities();

    PostOffice getEntityById(long id);

    PostOffice saveEntity(PostOffice postOffice);
    
    List<PostOfficeDto> getAll();
    
    PostOfficeDto getById(long id);
    
    PostOfficeDto save(PostOfficeDto postOfficeDto);
    
    PostOfficeDto update(long id, PostOfficeDto postOfficeDto);
    
    boolean delete(long id);
}
