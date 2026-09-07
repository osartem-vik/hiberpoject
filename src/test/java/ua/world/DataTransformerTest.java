package ua.world;

import org.junit.jupiter.api.Test;
import ua.world.domain.City;
import ua.world.domain.Continent;
import ua.world.domain.Country;
import ua.world.domain.CountryLanguage;
import ua.world.redis.CityCountry;
import ua.world.redis.Language;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataTransformerTest {

    private final DataTransformer transformer = new DataTransformer();

    @Test
    void transform_copiesCityAndCountryFieldsAndLanguages() {
        Country country = new Country();
        country.setCode("UKR");
        country.setAlternativeCode("UA");
        country.setName("Ukraine");
        country.setContinent(Continent.EUROPE);
        country.setRegion("Eastern Europe");
        country.setSurfaceArea(new BigDecimal("603500.00"));
        country.setPopulation(37_000_000);

        CountryLanguage ukrainian = new CountryLanguage();
        ukrainian.setLanguage("Ukrainian");
        ukrainian.setOfficial(true);
        ukrainian.setPercentage(new BigDecimal("67.5"));
        country.setLanguages(Set.of(ukrainian));

        City city = new City();
        city.setId(189);
        city.setName("Kyiv");
        city.setDistrict("Kyiv");
        city.setPopulation(2_800_000);
        city.setCountry(country);

        List<CityCountry> result = transformer.transform(List.of(city));

        assertEquals(1, result.size());
        CityCountry dto = result.get(0);
        assertEquals(189, dto.getId());
        assertEquals("Kyiv", dto.getName());
        assertEquals("Kyiv", dto.getDistrict());
        assertEquals(2_800_000, dto.getPopulation());
        assertEquals("UKR", dto.getCountryCode());
        assertEquals("UA", dto.getAlternativeCountryCode());
        assertEquals("Ukraine", dto.getCountryName());
        assertEquals(Continent.EUROPE, dto.getContinent());
        assertEquals("Eastern Europe", dto.getCountryRegion());
        assertEquals(new BigDecimal("603500.00"), dto.getCountrySurfaceArea());
        assertEquals(37_000_000, dto.getCountryPopulation());
        assertEquals(1, dto.getLanguages().size());

        Language language = dto.getLanguages().iterator().next();
        assertEquals("Ukrainian", language.getLanguage());
        assertTrue(language.getOfficial());
        assertEquals(new BigDecimal("67.5"), language.getPercentage());
    }
}
