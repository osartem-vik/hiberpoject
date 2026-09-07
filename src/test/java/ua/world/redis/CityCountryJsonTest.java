package ua.world.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ua.world.domain.Continent;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CityCountryJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void cityCountry_survivesJacksonRoundTrip() throws Exception {
        Language language = new Language();
        language.setLanguage("Ukrainian");
        language.setOfficial(true);
        language.setPercentage(new BigDecimal("67.5"));

        CityCountry original = new CityCountry();
        original.setId(189);
        original.setName("Kyiv");
        original.setDistrict("Kyiv");
        original.setPopulation(2_800_000);
        original.setCountryCode("UKR");
        original.setAlternativeCountryCode("UA");
        original.setCountryName("Ukraine");
        original.setContinent(Continent.EUROPE);
        original.setCountryRegion("Eastern Europe");
        original.setCountrySurfaceArea(new BigDecimal("603500.00"));
        original.setCountryPopulation(37_000_000);
        original.setLanguages(Set.of(language));

        String json = mapper.writeValueAsString(original);
        CityCountry restored = mapper.readValue(json, CityCountry.class);

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getCountryCode(), restored.getCountryCode());
        assertEquals(original.getContinent(), restored.getContinent());
        assertEquals(1, restored.getLanguages().size());
        Language restoredLang = restored.getLanguages().iterator().next();
        assertEquals("Ukrainian", restoredLang.getLanguage());
        assertTrue(Boolean.TRUE.equals(restoredLang.getOfficial())
                || Boolean.TRUE.equals(restoredLang.getIsOfficial()));
    }
}
