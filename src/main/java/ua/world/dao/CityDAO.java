package ua.world.dao;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ua.world.domain.City;
import ua.world.domain.Country;

import java.util.List;

public class CityDAO {
    private final SessionFactory sessionFactory;


    public CityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List <City> getItems(int offSet, int limit){
        Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c", City.class);
        query.setFirstResult(offSet);
        query.setMaxResults(limit);
        return query.list();
    }

    public int getTotalCount(){
        Query<Long> query = sessionFactory.getCurrentSession().createQuery("select count (c) from City c", Long.class);
        return Math.toIntExact(query.uniqueResult());
    }

}
