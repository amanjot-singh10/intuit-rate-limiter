package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.exception.RateNotFound;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SlidingWindowRateLimiterTest {

    private SlidingWindowRateLimiter rateLimiterTest;
    private RateLimiterRedisConnection rateLimiterRedisConnectionMock;
    private RedissonClient redissonClientMock;

    @BeforeEach
    public void setup() throws FileLoadException {
        rateLimiterRedisConnectionMock = mock(RateLimiterRedisConnection.class);
        redissonClientMock = mock(RedissonClient.class);
        when(rateLimiterRedisConnectionMock.getRedisClient()).thenReturn(redissonClientMock);
        rateLimiterTest = new SlidingWindowRateLimiter(rateLimiterRedisConnectionMock);
    }

    @Test
    public void testTryConsume() throws RateNotFound {
        String key = "testKey";
        SlidingWindowRateLimiter rateLimiterSpy = spy(rateLimiterTest);
        List<Object> mock1 = new ArrayList<>();
        mock1.add("10");mock1.add("60"); mock1.add("9");
        List<Object> mock = new ArrayList<>();
        mock.add("ALLOW"); mock.add(mock1);

        RScript scriptMock = mock(RScript.class);
        when(redissonClientMock.getScript(StringCodec.INSTANCE)).thenReturn(scriptMock);
        Object[] params = new Object[] {10, 60};
        when(scriptMock.eval(anyString(), eq(RScript.Mode.READ_WRITE), anyString() , eq(RScript.ReturnType.MULTI), anyList(), eq(params)))
                .thenReturn(mock); // Adjust the values as needed

        Rate result = rateLimiterSpy.checkLimit(key,10, 60);

        assertNotNull(result);
        assertEquals(RateLimitStatus.ALLOW, result.getStatus());
        assertEquals("60", result.getRefreshInterval());
        assertEquals("9", result.getRemaining());

        verify(scriptMock).eval(anyString(),
                eq(RScript.Mode.READ_WRITE),
                anyString(),
                eq(RScript.ReturnType.MULTI),
                anyList(),
                eq(params));
    }
}