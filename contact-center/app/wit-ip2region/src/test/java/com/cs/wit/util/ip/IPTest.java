package com.cs.wit.util.ip;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Unit test for simple App.
 */
public class IPTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void ip() {
        IPTools tools = new IPTools(new AnnotationConfigApplicationContext());
        tools.setup();
        System.out.println(tools.findGeography("157.0.45.35"));
    }
}
