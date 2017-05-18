package com.opinta.service;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.entity.PostcodePool;

import java.util.List;

public interface PostcodePoolService {

    PostcodePool saveEntity(PostcodePool postcodePool);

    List<PostcodePoolDto> getAll();

    PostcodePoolDto getById(long id);

    PostcodePoolDto save(PostcodePoolDto postcodePoolDto);

    PostcodePoolDto update(long id, PostcodePoolDto postcodePoolDto);

    boolean delete(long id);

    boolean addBarcodeInnerNumbers(long postcodeId, List<BarcodeInnerNumberDto> barcodeInnerNumberDtos);
}
