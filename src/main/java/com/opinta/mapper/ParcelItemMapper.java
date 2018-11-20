package com.opinta.mapper;

import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ParcelItemMapper extends BaseMapper<ParcelItemDto, ParcelItem> {

    @Mappings({
            @Mapping(source = "parcel.id", target = "parcelId")
    })
    ParcelItemDto toDto(ParcelItem parcelItem);

    @Mappings({
            @Mapping(target = "parcel", expression = "java(createParcelById(parcelItemDto.getParcelId()))")
    })
    ParcelItem toEntity(ParcelItemDto parcelItemDto);

    default Parcel createParcelById(long id) {
        Parcel parcel = new Parcel();
        parcel.setId(id);
        return parcel;
    }
}
