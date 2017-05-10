package com.opinta.dao;


import com.opinta.entity.Parcel;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public class ParcelDaoImpl implements ParcelDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public ParcelDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
@Transactional
    @Override
    public List<Parcel> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Parcel.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }
    @Transactional
    @Override
    public Parcel getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (Parcel) session.get(Parcel.class, id);
    }
    @Transactional
    @Override
    public Parcel save(Parcel parcel) {
        Session session = sessionFactory.getCurrentSession();
        return (Parcel) session.merge(parcel);
    }
    @Transactional
    @Override
    public void update(Parcel parcel) {
        Session session = sessionFactory.getCurrentSession();
        session.update(parcel);

    }
    @Transactional
    @Override
    public void delete(Parcel parcel) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(parcel);

    }
}
