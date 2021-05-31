package com.example.weather.controller;

import com.example.weather.service.LiveWeatherService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("weather")
public class WeatherController {

    @Autowired
    private LiveWeatherService weatherService;

    @ApiOperation("Return a JSON object that gives the weather averages.")
    @GetMapping(value = "/forecast", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> weatherForecastAverage(@ApiParam("City's name") String city){
        return weatherService.weatherForecastAverage(city);
    }

    @ApiOperation("Return the JSON object that gives all available cities.")
    @GetMapping(value = "/allCities")
    public ResponseEntity<?> getAllCities() throws IOException {
        return weatherService.getAllCites();
    }
}
