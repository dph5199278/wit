package com.cs.wit.util.mobile;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Unit test for simple App.
 */
public class MobileNumberTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void number() {
        MobileNumberTools tools = new MobileNumberTools(new AnnotationConfigApplicationContext());
        tools.setup();
        tools.getAddress("18099999999")
            .ifPresent(address -> {
                System.out.println("Areacode:" + address.getAreacode());
                System.out.println("Code:" + address.getCode());
                System.out.println("Id:" + address.getId());
                System.out.println("City:" + address.getCity());
                System.out.println("Country:" + address.getCountry());
                System.out.println("Isp:" + address.getIsp());
                System.out.println("Province:" + address.getProvince());
                System.out.println("Zipcode:" + address.getZipcode());
            });
    }
}
