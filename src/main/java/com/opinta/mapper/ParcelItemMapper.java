package com.opinta.mapper;

import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.ParcelItem;
import org.mapstruct.Mapper;

/**
 * Created by Dmytro Kushnir on 13.05.17.
 */
@Mapper(componentModel = "spring")
public interface ParcelItemMapper extends BaseMapper<ParcelItemDto, ParcelItem> {
}
