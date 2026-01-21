package spring_masters.attendance_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spring_masters.attendance_system.dto.GeocodingResponseDTO;
import spring_masters.attendance_system.dto.WeatherResponseDTO;

@Service
public class WeatherService {

    @Value("${app.weather.geocoding-url}")
    private String geocodingUrl;

    @Value("${app.weather.forecast-url}")
    private String forecastUrl;

    private final RestTemplate restTemplate;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    public WeatherResponseDTO getWeather(String city) {
        try {
            // 1. Get Coordinates from Geocoding API
            String geoUrl = UriComponentsBuilder.fromUriString(geocodingUrl)
                    .queryParam("name", city)
                    .queryParam("count", 1)
                    .queryParam("language", "en")
                    .queryParam("format", "json")
                    .toUriString();

            System.out.println("WeatherService: Requesting coordinates for city: [" + city + "]");
            GeocodingResponseDTO geoResponse = restTemplate.getForObject(geoUrl, GeocodingResponseDTO.class);

            if (geoResponse != null && geoResponse.getResults() != null && !geoResponse.getResults().isEmpty()) {
                System.out.println("WeatherService: Found " + geoResponse.getResults().size() + " results for " + city);
            }

            if (geoResponse == null || geoResponse.getResults() == null || geoResponse.getResults().isEmpty()) {
                System.err.println("No geocoding results found for city: " + city);
                return null;
            }

            GeocodingResponseDTO.Result location = geoResponse.getResults().get(0);
            System.out.println("WeatherService: Resolved city [" + city + "] to [" + location.getName() + " ("
                    + location.getCountry() + ")]");
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            // 2. Get Weather from Forecast API
            String weatherUrl = UriComponentsBuilder.fromUriString(forecastUrl)
                    .queryParam("latitude", lat)
                    .queryParam("longitude", lon)
                    .queryParam("current_weather", true)
                    .toUriString();

            System.out.println("WeatherService: Calling Open-Meteo API: " + weatherUrl);
            return restTemplate.getForObject(weatherUrl, WeatherResponseDTO.class);

        } catch (Exception e) {
            System.err.println("Error fetching weather from Open-Meteo: " + e.getMessage());
            return null;
        }
    }
}
