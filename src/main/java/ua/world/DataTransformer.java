package ua.world;

import ua.world.domain.City;
import ua.world.domain.Country;
import ua.world.domain.CountryLanguage;
import ua.world.redis.CityCountry;
import ua.world.redis.Language;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataTransformer {
    public List<CityCountry> transform(List<City> cities) {
        return cities.stream().map(this::toCityCountry).collect(Collectors.toList());
    }

    public CityCountry toCityCountry(City city) {
        CityCountry res = new CityCountry();
        res.setId(city.getId());
        res.setName(city.getName());
        res.setPopulation(city.getPopulation());
        res.setDistrict(city.getDistrict());

        Country country = city.getCountry();
        res.setAlternativeCountryCode(country.getAlternativeCode());
        res.setContinent(country.getContinent());
        res.setCountryCode(country.getCode());
        res.setCountryName(country.getName());
        res.setCountryPopulation(country.getPopulation());
        res.setCountryRegion(country.getRegion());
        res.setCountrySurfaceArea(country.getSurfaceArea());

        Set<Language> languages = country.getLanguages().stream()
                .map(this::toLanguage)
                .collect(Collectors.toSet());
        res.setLanguages(languages);
        return res;
    }

    private Language toLanguage(CountryLanguage cl) {
        Language language = new Language();
        language.setLanguage(cl.getLanguage());
        language.setOfficial(cl.getOfficial());
        language.setPercentage(cl.getPercentage());
        return language;
    }
}
