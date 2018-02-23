package com.opinta.mapper;

import com.opinta.dto.ParcelDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Client;
import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ShipmentMapper extends BaseMapper<ShipmentDto, Shipment> {

    @Override
    @Mappings({
            @Mapping(source = "sender.id", target = "senderId"),
            @Mapping(source = "recipient.id", target = "recipientId"),
            @Mapping(target = "parcels", expression = "java(createParcelDtoList(shipment.getParcels()))")
    })
    ShipmentDto toDto(Shipment shipment);

    @Override
    @Mappings({
            @Mapping(target = "sender", expression = "java(createClientById(shipmentDto.getSenderId()))"),
            @Mapping(target = "recipient", expression = "java(createClientById(shipmentDto.getRecipientId()))"),
            @Mapping(target = "parcels", expression = "java(createParcelList(shipmentDto.getParcels()))")
    })
    Shipment toEntity(ShipmentDto shipmentDto);

    default Client createClientById(long id) {
        Client client = new Client();
        client.setId(id);
        return client;
    }

    default List<ParcelDto> createParcelDtoList(List<Parcel> parcelList) {
        List<ParcelDto> parcelDtos = new ArrayList<>();
        ParcelMapper mapper = new ParcelMapperImpl();
        for (Parcel parcel :
                parcelList) {
            parcelDtos.add(mapper.toDto(parcel));
        }
        return parcelDtos;
    }

    default List<Parcel> createParcelList(List<ParcelDto> parcelDtoList) {
        List<Parcel> parcels = new ArrayList<>();
        ParcelMapper mapper = new ParcelMapperImpl();
        for (ParcelDto parcelDto :
                parcelDtoList) {
            parcels.add(mapper.toEntity(parcelDto));
        }
        return parcels;
    }
}
