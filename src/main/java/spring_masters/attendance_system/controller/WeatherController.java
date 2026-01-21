package spring_masters.attendance_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring_masters.attendance_system.dto.WeatherResponseDTO;
import spring_masters.attendance_system.service.WeatherService;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "Weather", description = "Weather Information API (Open-Meteo)")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    @Operation(summary = "Get current weather for a city")
    public ResponseEntity<WeatherResponseDTO> getWeather(@RequestParam(defaultValue = "London") String city) {
        System.out.println("WeatherController: Received request for city: " + city);
        WeatherResponseDTO weather = weatherService.getWeather(city);
        if (weather != null) {
            return ResponseEntity.ok(weather);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
