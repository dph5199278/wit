package org.springframework.util.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DataSizeTest {

    @Test
    void test() {
        DataSize parsed = DataSize.parse("15MB");
        assertEquals(15, parsed.toMegabytes());
    }
}
