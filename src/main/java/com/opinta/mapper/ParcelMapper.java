package com.opinta.mapper;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ParcelItemMapper.class})
public interface ParcelMapper extends BaseMapper<ParcelDto, Parcel> {
}
