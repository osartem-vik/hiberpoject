package ua.world.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.world.domain.Country;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryDAOTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Query<Country> query;

    private CountryDAO countryDAO;

    @BeforeEach
    void setUp() {
        countryDAO = new CountryDAO(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getAll_returnsListFromQuery() {
        List<Country> expected = List.of(new Country(), new Country());
        when(session.createQuery("select c from Country c", Country.class)).thenReturn(query);
        when(query.list()).thenReturn(expected);

        assertEquals(expected, countryDAO.getAll());
    }
}