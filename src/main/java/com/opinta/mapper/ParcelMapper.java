package com.opinta.mapper;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {ParcelItemMapper.class}, componentModel = "spring")
public interface ParcelMapper extends BaseMapper<ParcelDto, Parcel> {
    @Override
    @Mappings({
            @Mapping(source = "parcelItems", target = "parcelItems")
    })
    ParcelDto toDto(Parcel parcel);

    @Override
    @Mappings({
            @Mapping(source = "parcelItems", target = "parcelItems")
    })
    Parcel toEntity(ParcelDto parcelDto);
}
