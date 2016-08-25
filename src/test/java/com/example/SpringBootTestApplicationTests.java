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
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringBootTestApplication.class)
@WebIntegrationTest(randomPort = true)
public class SpringBootTestApplicationTests {
    @Autowired
    HotelRepository hotelRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    HotelService hotelService;

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
        Assert.assertTrue(!content.isEmpty());
    }


    RestTemplate template = new TestRestTemplate();

    @Test
    public void testMvc() {
        ResponseEntity<City> entity = template.getForEntity("http://localhost:" + port + "/api/sample/helloworld", City.class);
        System.out.println(entity.getBody().getName());
    }

}
