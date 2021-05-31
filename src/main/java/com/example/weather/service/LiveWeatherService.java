package com.example.weather.service;

import com.example.weather.dto.WeatherAverageDTO;
import com.example.weather.dto.WeatherMapDTO;

import com.example.weather.dto.WeatherMapTimeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.json.Json;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LiveWeatherService {
    private final String URL = "http://api.openweathermap.org/data/2.5/forecast";

    @Value("${weather.apikey}")
    private String apiKey;

    private RestTemplate restTemplate;

    public LiveWeatherService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity<?> weatherForecastAverage(String city){
        List<WeatherAverageDTO> result = new ArrayList<WeatherAverageDTO>();

        try {

            WeatherMapDTO weatherMap = this.restTemplate.getForObject(this.url(city), WeatherMapDTO.class);

            for (LocalDate reference = LocalDate.now(); reference.isBefore(LocalDate.now().plusDays(5)); reference = reference.plusDays(1)) {
                final LocalDate ref = reference;
                List<WeatherMapTimeDTO> collect = weatherMap.getList().stream().filter(x ->
                        x.getDt().toLocalDate().equals(ref)
                ).collect(Collectors.toList());

                if(!CollectionUtils.isEmpty(collect)) {

                    result.add(this.average(collect));
                }
            }
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(new Json(e.getResponseBodyAsString()), e.getStatusCode());
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    //Reading all available cities
    public ResponseEntity<?> getAllCites() throws IOException {
        File resource = new ClassPathResource("JSON/city.list.json").getFile();
        String file = readFile(resource);

        return new ResponseEntity<>(file, HttpStatus.OK);
    }

    private WeatherAverageDTO average(List<WeatherMapTimeDTO> list) {
        WeatherAverageDTO result = new WeatherAverageDTO();

        for (WeatherMapTimeDTO item : list) {
            result.setDate(item.getDt().toLocalDate());
            result.plusMap(item);
        }

        result.totalize();

        return result;
    }

    private static String readFile(File path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(String.valueOf(path)));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            // Instead of using default, pass in a decoder.
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }

    // units=metric - For temperature in Celsius
    private String url(String city) {
        return String.format(URL.concat("?q=%s").concat("&appid=%s").concat("&units=metric"), city, apiKey);
    }

}
