package com.opinta.dao;

import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmytro Kushnir on 13.05.17.
 */
@Repository
public class ParcelDaoImpl implements ParcelDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public ParcelDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Parcel> getAll(long shipmentId) {
        Session session = sessionFactory.getCurrentSession();
        Shipment currentShipment = (Shipment) session.get(Shipment.class, shipmentId);
        if (currentShipment == null) {
            return new ArrayList<>();
        }
        return currentShipment.getParcels();
    }

    @Override
    public Parcel getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (Parcel) session.get(Parcel.class, id);
    }

    @Override
    public Parcel save(Parcel parcel) {
        Session session = sessionFactory.getCurrentSession();
        return (Parcel) session.merge(parcel);
    }

    @Override
    public void update(Parcel parcel) {
        Session session = sessionFactory.getCurrentSession();
        session.update(parcel);
    }

    @Override
    public void delete(Parcel parcel) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(parcel);
    }
}
