package com.opinta.mapper;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {ParcelItemMapper.class})
public interface ParcelMapper extends BaseMapper<ParcelDto,Parcel> {

    @Override
    @Mappings({
            @Mapping(source = "shipment.id", target = "shipmentId"),
//            @Mapping(target = "parcelItems", expression = "java(createParcelItemDtoList(parcel.getParcelItems()))")
    })
    ParcelDto toDto(Parcel parcel);

    @Override
    @Mappings({
            @Mapping(target = "shipment", expression = "java(createShipmentById(parcelDto.getShipmentId()))"),
//            @Mapping(target = "parcelItems", expression = "java(createParcelItemList(parcelDto.getParcelItems()))")
    })
    Parcel toEntity(ParcelDto parcelDto);

    default Shipment createShipmentById(long id) {
        Shipment shipment = new Shipment();
        shipment.setId(id);
        return shipment;
    }

//    default List<ParcelItemDto> createParcelItemDtoList(List<ParcelItem> parcelItemList) {
//        List<ParcelItemDto> parcelItemsDto = new ArrayList<>();
//        ParcelItemMapper mapper = new ParcelItemMapperImpl();
//        for (ParcelItem parcelItem :
//                parcelItemList) {
//            parcelItemsDto.add(mapper.toDto(parcelItem));
//        }
//        return parcelItemsDto;
//    }
//
//    default List<ParcelItem> createParcelItemList(List<ParcelItemDto> parcelItemDtoList) {
//        List<ParcelItem> parcelItems = new ArrayList<>();
//        ParcelItemMapper mapper = new ParcelItemMapperImpl();
//        for (ParcelItemDto parcelItemDto :
//                parcelItemDtoList) {
//            parcelItems.add(mapper.toEntity(parcelItemDto));
//        }
//        return parcelItems;
//    }
}
