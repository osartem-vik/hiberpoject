package ua.world.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.world.domain.City;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityDAOTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Query<City> cityQuery;
    @Mock
    private Query<Long> countQuery;

    private CityDAO cityDAO;

    @BeforeEach
    void setUp() {
        cityDAO = new CityDAO(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getItems_appliesOffsetAndLimit() {
        List<City> expected = List.of(new City());
        when(session.createQuery("select c from City c", City.class)).thenReturn(cityQuery);
        when(cityQuery.list()).thenReturn(expected);

        List<City> actual = cityDAO.getItems(500, 100);

        assertSame(expected, actual);
        verify(cityQuery).setFirstResult(500);
        verify(cityQuery).setMaxResults(100);
    }

    @Test
    void getTotalCount_convertsLongToInt() {
        when(session.createQuery("select count (c) from City c", Long.class)).thenReturn(countQuery);
        when(countQuery.uniqueResult()).thenReturn(4079L);

        assertEquals(4079, cityDAO.getTotalCount());
    }

    @Test
    void getById_setsIdParameterAndReturnsCity() {
        City city = new City();
        city.setId(10);
        when(session.createQuery(
                "select c from City c join fetch c.country where c.id = :ID",
                City.class
        )).thenReturn(cityQuery);
        when(cityQuery.getSingleResult()).thenReturn(city);

        City actual = cityDAO.getById(10);

        assertSame(city, actual);
        verify(cityQuery).setParameter(eq("ID"), eq(10));
    }
}
