package com.intuit.ratelimiter.helper;


import com.intuit.ratelimiter.utils.RateLimiterPropertiesUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyMakerTest {

    public static final String SCRIPT = "application-ratelimit-SCRIPT.yml";
    private static KeyMaker keyMaker;

    public KeyMakerTest() {
        keyMaker = new DefaultKeyMaker();
    }

    @ParameterizedTest
    @ValueSource(strings = {"fixed", "sliding"})
    public void keyOnlyServiceTest(String algo) throws IOException {
        String keyOnlyService = keyMaker.key(RateLimiterPropertiesUtil.getRateLimiterProperty(SCRIPT.replace("SCRIPT", algo)),
                "serviceA", "clientNotPresent");
        final StringJoiner joiner = new StringJoiner("-");
        joiner.add("serviceA");
        joiner.add(algo);
        assertEquals(joiner.toString(), keyOnlyService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"fixed", "sliding"})
    public void keyClientAndServiceTest(String algo) throws IOException {
        String keyBothService = keyMaker.key(RateLimiterPropertiesUtil.getRateLimiterProperty(SCRIPT.replace("SCRIPT", algo)),
                "serviceA", "testA");
        final StringJoiner joiner = new StringJoiner("-");
        joiner.add("serviceA");
        joiner.add("testA");
        joiner.add(algo);
        assertEquals(joiner.toString(), keyBothService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"fixed", "sliding"})
    public void keyInvalidTest(String algo) throws IOException {
        String keyInvalid = keyMaker.key(RateLimiterPropertiesUtil.getRateLimiterProperty(SCRIPT.replace("SCRIPT", algo)),
                "serviceNotPresent", "clientNotPresent");
        assertEquals("", keyInvalid);
    }
}
