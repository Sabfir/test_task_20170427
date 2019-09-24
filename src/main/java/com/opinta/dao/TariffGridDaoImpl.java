package com.opinta.dao;

import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TariffGridDaoImpl implements TariffGridDao {
    public static final String ID = "id";
    public static final String W_2_W_VARIATION = "w2wVariation";
    private final SessionFactory sessionFactory;

    @Autowired
    public TariffGridDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TariffGrid> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(TariffGrid.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public TariffGrid getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (TariffGrid) session.get(TariffGrid.class, id);
    }

    @Override
    public TariffGrid save(TariffGrid tariffGrid) {
        Session session = sessionFactory.getCurrentSession();
        return (TariffGrid) session.merge(tariffGrid);
    }

    @Override
    public void update(TariffGrid tariffGrid) {
        Session session = sessionFactory.getCurrentSession();
        session.update(tariffGrid);
    }

    @Override
    public void delete(TariffGrid tariffGrid) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(tariffGrid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TariffGrid getByDimension(float weight, float length, W2wVariation w2wVariation) {
        String id = ID;
        Session session = sessionFactory.getCurrentSession();
        DetachedCriteria minId = DetachedCriteria.forClass(TariffGrid.class).setProjection(Projections.min(id));
        return (TariffGrid) session.createCriteria(TariffGrid.class)
                .add(Restrictions.and(Restrictions.ge("weight", weight),
                        Restrictions.ge("length", length),
                        Restrictions.eq(W_2_W_VARIATION, w2wVariation)))
                .addOrder(Order.asc(id))
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public TariffGrid getLast(W2wVariation w2wVariation) {
        String id = ID;
        Session session = sessionFactory.getCurrentSession();
        return (TariffGrid) session.createCriteria(TariffGrid.class)
                .add(Restrictions.eq(W_2_W_VARIATION, w2wVariation))
                .addOrder(Order.desc(id))
                .setMaxResults(1)
                .uniqueResult();
    }
}
