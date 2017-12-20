package com.opinta.mapper;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ParcelMapper extends BaseMapper<ParcelDto, Parcel>{

    @Override
    @Mapping(source = "shipment.id", target = "shipmentId")
    ParcelDto toDto (Parcel parcel);

    @Override
    @Mapping(target = "shipment", expression = "java(createShipmentById(parcelDto.getShipmentId()))")
    Parcel toEntity (ParcelDto parcelDto);

    default Shipment createShipmentById(long id){
        Shipment shipment = new Shipment();
        shipment.setId(id);
        return shipment;
    }
}
