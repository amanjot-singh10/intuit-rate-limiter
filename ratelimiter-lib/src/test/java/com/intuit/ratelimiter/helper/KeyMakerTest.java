package com.intuit.ratelimiter.helper;


import com.intuit.ratelimiter.constants.RateLimiterType;
import com.intuit.ratelimiter.utils.RateLimiterPropertiesUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
    @EnumSource(RateLimiterType.class)
    public void keyOnlyServiceTest(RateLimiterType algo) throws IOException {
        String keyOnlyService = keyMaker.key(RateLimiterPropertiesUtil.getRateLimiterProperty(SCRIPT.replace("SCRIPT", algo.name())),
                "serviceA", "clientNotPresent");
        final StringJoiner joiner = new StringJoiner("-");
        joiner.add("serviceA");
        joiner.add(algo.getKeySuffix());
        assertEquals(joiner.toString(), keyOnlyService);
    }

    @ParameterizedTest
    @EnumSource(RateLimiterType.class)
    public void keyClientAndServiceTest(RateLimiterType algo) throws IOException {
        String keyBothService = keyMaker.key(RateLimiterPropertiesUtil.getRateLimiterProperty(SCRIPT.replace("SCRIPT", algo.name())),
                "serviceA", "testA");
        final StringJoiner joiner = new StringJoiner("-");
        joiner.add("serviceA");
        joiner.add("testA");
        joiner.add(algo.getKeySuffix());
        assertEquals(joiner.toString(), keyBothService);
    }

    @ParameterizedTest
    @EnumSource(RateLimiterType.class)
    public void keyInvalidTest(RateLimiterType algo) throws IOException {
        String keyInvalid = keyMaker.key(RateLimiterPropertiesUtil.getRateLimiterProperty(SCRIPT.replace("SCRIPT", algo.name())),
                "serviceNotPresent", "clientNotPresent");
        assertEquals("", keyInvalid);
    }
}
