package com.opinta.mapper;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = ParcelItemMapper.class)
public interface ParcelMapper extends BaseMapper<ParcelDto, Parcel> {

	@Override
	@Mappings({ @Mapping(source = "id", target = "parcelId") })
	ParcelDto toDto(Parcel parcel);

	@Override
	Parcel toEntity(ParcelDto parcelDto);

}
