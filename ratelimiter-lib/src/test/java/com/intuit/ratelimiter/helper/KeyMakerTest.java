package com.intuit.ratelimiter.helper;


import com.intuit.ratelimiter.properties.RateLimiterPropertiesTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyMakerTest {

    private static KeyMaker keyMaker;

    @BeforeAll
    public static void init(){
        keyMaker = new DefaultKeyMaker();
    }

    @Test
    public void keyOnlyServiceTest(){
        String keyOnlyService = keyMaker.key(RateLimiterPropertiesTest.getRateLimiterProperty(),"serviceA", "clientNotPresent");
        assertEquals("serviceA-sliding", keyOnlyService);
    }

    @Test
    public void keyClientAndServiceTest(){
        String keyBothService = keyMaker.key(RateLimiterPropertiesTest.getRateLimiterProperty(),"serviceA", "clientA");
        assertEquals("serviceA-clientA-sliding", keyBothService);
    }

    @Test
    public void keyInvalidTest(){
        String keyInvalid = keyMaker.key(RateLimiterPropertiesTest.getRateLimiterProperty(),"serviceNotPresent", "clientNotPresent");
        assertEquals("", keyInvalid);
    }

}
