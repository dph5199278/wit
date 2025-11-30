package com.cs.wit.util.ip;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Unit test for simple App.
 */
public class IPTest {

    /**
     * IPV4 test
     */
    @Test
    public void ipv4() {
        IP ip = ip("157.0.45.35");
        System.out.println("IPV4: ");
        System.out.println(ip.getCountry());
        System.out.println(ip.getProvince());
        System.out.println(ip.getCity());
        System.out.println(ip.getIsp());
        System.out.println(ip.getRegion());
    }

    /**
     * IPV6 test
     */
    @Test
    public void ipv6() {
        IP ip = ip("2408:843c:4600::1");
        System.out.println("IPV6: ");
        System.out.println(ip.getCountry());
        System.out.println(ip.getProvince());
        System.out.println(ip.getCity());
        System.out.println(ip.getIsp());
        System.out.println(ip.getRegion());
    }

    private IP ip(String ip) {
        IPTools tools = new IPTools(new AnnotationConfigApplicationContext());
        tools.setup();
        return tools.findGeography(ip);
    }
}
