package spring_masters.attendance_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponseDTO {
    private double latitude;
    private double longitude;

    @JsonProperty("current_weather")
    private CurrentWeather currentWeather;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentWeather {
        private double temperature;
        private double windspeed;
        private int weathercode;
        private String time;

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public double getWindspeed() {
            return windspeed;
        }

        public void setWindspeed(double windspeed) {
            this.windspeed = windspeed;
        }

        public int getWeathercode() {
            return weathercode;
        }

        public void setWeathercode(int weathercode) {
            this.weathercode = weathercode;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
