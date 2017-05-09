package com.opinta.mapper;

import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.ParcelItem;
import org.mapstruct.Mapper;

/**
 * Created by Мария on 09.05.2017.
 */
@Mapper(componentModel = "spring")
public interface ParcelItemMapper extends BaseMapper<ParcelItemDto, ParcelItem> {
}