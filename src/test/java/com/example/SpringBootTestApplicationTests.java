package com.example;

import com.example.entity.City;
import com.example.entity.Hotel;
import com.example.repository.CityRepository;
import com.example.repository.HotelRepository;
import com.example.service.HotelService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBootTestApplicationTests {
    @Autowired
    protected HotelRepository hotelRepository;
    @Autowired
    protected CityRepository cityRepository;
    @Autowired
    protected HotelService hotelService;
    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Value("${local.server.port}")
    int port;

    @Test
    public void contextLoads() {
        PageRequest pageRequest = new PageRequest(0, 100);
        Page<Hotel> hotelPage = hotelRepository.queryPage("From Hotel h", null, pageRequest, null);
        List<Hotel> content = hotelPage.getContent();
        for (int i = 0; i < content.size(); i++) {
            Hotel hotel = content.get(i);
            System.out.println(hotel.getName());
        }

        Page<HashMap> hashMapPage = hotelRepository.queryPageBySql("select * from hotel h", new PageRequest(0, 10), new HashMap<>(), HashMap.class);

        Page<Hotel> hotelPage2 = hotelRepository.queryPageBySql("select * from hotel h", new PageRequest(0, 10), new HashMap<>(), Hotel.class);
        Assert.assertTrue(!content.isEmpty());
    }

    @Test
    public void testMvc() {
        ResponseEntity<City> entity = testRestTemplate.getForEntity("http://localhost:" + port + "/api/sample/helloworld", City.class);
        System.out.println(entity.getBody().getName());
    }

    static class HotelTest {
        //String
    }

}
