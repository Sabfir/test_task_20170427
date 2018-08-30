package com.opinta.mapper;

import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.ParcelItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ParcelItemMapper extends BaseMapper<ParcelItemDto, ParcelItem> {

	@Override
	@Mappings({ @Mapping(source = "id", target = "parcelItemId") })
	ParcelItemDto toDto(ParcelItem ParcelItem);

	@Override
	ParcelItem toEntity(ParcelItemDto parcelItemDto);

}
