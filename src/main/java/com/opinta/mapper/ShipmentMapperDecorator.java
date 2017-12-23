package com.opinta.mapper;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.Shipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class ShipmentMapperDecorator implements ShipmentMapper {

    @Autowired
    @Qualifier("delegate")
    private ShipmentMapper delegate;

    @Override
    public Shipment toEntity(ShipmentDto shipmentDto) {
        Shipment shipment = delegate.toEntity(shipmentDto);
        for (Parcel parcel : shipment.getParcels()) {
            parcel.setShipment(shipment);
            /*for (ParcelItem item : parcel.getItems()) {
                item.setParcel(parcel);
            }*/
        }
        return shipment;
    }
}
