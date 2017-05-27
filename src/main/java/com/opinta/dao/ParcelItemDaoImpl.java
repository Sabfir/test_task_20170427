package com.opinta.dao;

import com.opinta.dao.ParcelItemDao;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
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
public class ParcelItemDaoImpl implements ParcelItemDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public ParcelItemDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<ParcelItem> getAll(long parcelId) {
        Session session = sessionFactory.getCurrentSession();
        Parcel currentParcel = (Parcel) session.get(Parcel.class, parcelId);
        if (currentParcel == null) {
            return new ArrayList<>();
        }
        return currentParcel.getParcelItems();
    }

    @Override
    public ParcelItem getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (ParcelItem) session.get(ParcelItem.class, id);
    }

    @Override
    public ParcelItem save(ParcelItem parcelItem) {
        Session session = sessionFactory.getCurrentSession();
        return (ParcelItem) session.merge(parcelItem);
    }

    @Override
    public void update(ParcelItem parcelItem) {
        Session session = sessionFactory.getCurrentSession();
        session.update(parcelItem);
    }

    @Override
    public void delete(ParcelItem parcelItem) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(parcelItem);
    }
}
