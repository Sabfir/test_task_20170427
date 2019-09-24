package com.opinta.util;

import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Component
public class ShipmentInterceptor extends EmptyInterceptor {
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
                                String[] propertyNames, org.hibernate.type.Type[] types) {
        log.info("Log INFO onFlushDirty interception - " + entity.getClass().getName());
        if (entity instanceof Shipment) {

            List<Parcel> list = ((Shipment) entity).getParcelList();
            log.info("Log INFO ParcelList - " + list.toString());
        }
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
}
