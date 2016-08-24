package com.example;

import com.example.entity.Hotel;
import com.example.repository.HotelRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringBootTestApplication.class)
public class SpringBootTestApplicationTests {
    @Autowired
    HotelRepository hotelRepository;

    @Test
    public void contextLoads() {
        PageRequest pageRequest = new PageRequest(1, 10);
        Page<Hotel> hotelPage = hotelRepository.queryPage("From Hotel h", null, pageRequest, null);
        hotelPage.getContent().forEach(hotel -> {
            System.out.println(hotel.getName());
        });
        Assert.assertTrue(!hotelPage.getContent().isEmpty());
    }

}
