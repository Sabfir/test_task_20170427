package com.opinta.mapper;

import com.opinta.dto.ParcelDto;
import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ParcelMapper extends BaseMapper<ParcelDto, Parcel> {

    @Override
    @Mapping(target = "items", expression = "java(createParcelItemDtoList(parcel.getItems()))")
    ParcelDto toDto(Parcel parcel);

    @Override
    @Mapping(target = "items", expression = "java(createParcelItemList(parcelDto.getItems()))")
    Parcel toEntity(ParcelDto parcelDto);

    default List<ParcelItemDto> createParcelItemDtoList(List<ParcelItem> parcelItemList) {
        List<ParcelItemDto> parcelItemDtos = new ArrayList<>();
        ParcelItemMapper mapper = new ParcelItemMapperImpl();
        for (ParcelItem parcelItem :
                parcelItemList) {
            parcelItemDtos.add(mapper.toDto(parcelItem));
        }
        return parcelItemDtos;
    }

    default List<ParcelItem> createParcelItemList(List<ParcelItemDto> parcelItemDtoList) {
        List<ParcelItem> parcelItems = new ArrayList<>();
        ParcelItemMapper mapper = new ParcelItemMapperImpl();
        for (ParcelItemDto parcelItemDto :
                parcelItemDtoList) {
            parcelItems.add(mapper.toEntity(parcelItemDto));
        }
        return parcelItems;
    }
}
