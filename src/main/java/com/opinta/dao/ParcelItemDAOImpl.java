package com.opinta.dao;

import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ParcelItemDAOImpl implements ParcelItemDAO {
    private final SessionFactory sessionFactory;

    @Autowired
    public ParcelItemDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ParcelItem> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(ParcelItem.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ParcelItem> getAllByParcel(Parcel parcel) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(ParcelItem.class)
                .add(Restrictions.eq("parcel", parcel))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public ParcelItem getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (ParcelItem) session.get(ParcelItem.class, id);
    }

    @Override
    public ParcelItem save(ParcelItem item) {
        Session session = sessionFactory.getCurrentSession();
        return (ParcelItem) session.merge(item);
    }

    @Override
    public ParcelItem merge(ParcelItem item) {
        Session session = sessionFactory.getCurrentSession();
        return (ParcelItem) session.merge(item);
    }

    @Override
    public void delete(ParcelItem item) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(item);
    }
}
