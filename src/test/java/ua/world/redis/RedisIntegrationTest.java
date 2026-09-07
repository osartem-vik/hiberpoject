package ua.world.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import ua.world.domain.Continent;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RedisIntegrationTest {

    private static RedisClient client;
    private static ObjectMapper mapper;

    static boolean redisAvailable() {
        try {
            RedisClient probe = RedisClient.create(RedisURI.create("localhost", 6379));
            try (StatefulRedisConnection<String, String> connection = probe.connect()) {
                return "PONG".equalsIgnoreCase(connection.sync().ping());
            } finally {
                probe.shutdown();
            }
        } catch (Exception e) {
            return false;
        }
    }

    @BeforeAll
    static void init() {
        if (redisAvailable()) {
            client = RedisClient.create(RedisURI.create("localhost", 6379));
            mapper = new ObjectMapper();
        }
    }

    @AfterAll
    static void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }

    @Test
    @EnabledIf("redisAvailable")
    void pushAndReadCityCountry() throws Exception {
        CityCountry dto = new CityCountry();
        dto.setId(3);
        dto.setName("Test City");
        dto.setDistrict("Test");
        dto.setPopulation(1000);
        dto.setCountryCode("TST");
        dto.setAlternativeCountryCode("TS");
        dto.setCountryName("Testland");
        dto.setContinent(Continent.EUROPE);
        dto.setCountryRegion("Test region");
        dto.setCountrySurfaceArea(new BigDecimal("10.5"));
        dto.setCountryPopulation(5000);
        dto.setLanguages(Set.of());

        try (StatefulRedisConnection<String, String> connection = client.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            sync.set("3", mapper.writeValueAsString(dto));

            String json = sync.get("3");
            CityCountry restored = mapper.readValue(json, CityCountry.class);

            assertNotNull(restored);
            assertEquals("Test City", restored.getName());
            assertEquals("TST", restored.getCountryCode());
            sync.getdel("3");
        }
    }
}
