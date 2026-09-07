package ua.world.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.world.domain.City;
import ua.world.domain.Continent;
import ua.world.domain.Country;
import ua.world.domain.CountryLanguage;
import ua.world.support.HibernateTestFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CityDAOIntegrationTest {

    private static SessionFactory sessionFactory;
    private CityDAO cityDAO;
    private CountryDAO countryDAO;

    @BeforeAll
    static void init() {
        sessionFactory = HibernateTestFactory.create();
    }

    @AfterAll
    static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);

        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.createQuery("delete from CountryLanguage").executeUpdate();
            session.createQuery("delete from City").executeUpdate();
            session.createQuery("delete from Country").executeUpdate();

            Country ukraine = new Country();
            ukraine.setId(1);
            ukraine.setCode("UKR");
            ukraine.setAlternativeCode("UA");
            ukraine.setName("Ukraine");
            ukraine.setContinent(Continent.EUROPE);
            ukraine.setRegion("Eastern Europe");
            ukraine.setSurfaceArea(new BigDecimal("603500"));
            ukraine.setPopulation(37_000_000);
            session.persist(ukraine);

            City kyiv = new City();
            kyiv.setName("Kyiv");
            kyiv.setDistrict("Kyiv");
            kyiv.setPopulation(2_800_000);
            kyiv.setCountry(ukraine);
            session.persist(kyiv);

            City lviv = new City();
            lviv.setName("Lviv");
            lviv.setDistrict("Lviv");
            lviv.setPopulation(720_000);
            lviv.setCountry(ukraine);
            session.persist(lviv);

            CountryLanguage language = new CountryLanguage();
            language.setCountry(ukraine);
            language.setLanguage("Ukrainian");
            language.setOfficial(true);
            language.setPercentage(new BigDecimal("67.5"));
            session.persist(language);

            ukraine.setLanguages(Set.of(language));
            session.getTransaction().commit();
        }
    }

    @Test
    void getTotalCount_returnsInsertedCities() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            assertEquals(2, cityDAO.getTotalCount());
            session.getTransaction().commit();
        }
    }

    @Test
    void getItems_supportsPagination() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<City> firstPage = cityDAO.getItems(0, 1);
            List<City> secondPage = cityDAO.getItems(1, 1);
            session.getTransaction().commit();

            assertEquals(1, firstPage.size());
            assertEquals(1, secondPage.size());
            assertNotNull(firstPage.get(0).getName());
        }
    }

    @Test
    void getById_fetchesCityWithCountry() {
        Integer id;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            id = session.createQuery("select c.id from City c where c.name = :name", Integer.class)
                    .setParameter("name", "Kyiv")
                    .getSingleResult();
            session.getTransaction().commit();
        }

        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            City city = cityDAO.getById(id);
            session.getTransaction().commit();

            assertEquals("Kyiv", city.getName());
            assertEquals("Ukraine", city.getCountry().getName());
        }
    }

    @Test
    void countryDao_getAll_returnsPersistedCountry() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<Country> countries = countryDAO.getAll();
            session.getTransaction().commit();

            assertEquals(1, countries.size());
            assertEquals("UKR", countries.get(0).getCode());
        }
    }
}