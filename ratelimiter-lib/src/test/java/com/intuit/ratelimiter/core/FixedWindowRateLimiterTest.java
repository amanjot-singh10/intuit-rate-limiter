package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.exception.RateProcessingException;
import com.intuit.ratelimiter.model.Rate;
import com.intuit.ratelimiter.redis.connection.RateLimiterRedisConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FixedWindowRateLimiterTest {

    private FixedWindowRateLimiter rateLimiterTest;
    private RateLimiterRedisConnection rateLimiterRedisConnectionMock;
    private RedissonClient redissonClientMock;

    public FixedWindowRateLimiterTest() throws FileLoadException {
        rateLimiterRedisConnectionMock = mock(RateLimiterRedisConnection.class);
        redissonClientMock = mock(RedissonClient.class);
        when(rateLimiterRedisConnectionMock.getRedisClient()).thenReturn(redissonClientMock);
        rateLimiterTest = new FixedWindowRateLimiter(rateLimiterRedisConnectionMock);
    }

    @Test
    public void testTryConsume() {
        // Arrange
        String key = "testKey";
        FixedWindowRateLimiter rateLimiterSpy = spy(rateLimiterTest);
        List<Object> mock1 = new ArrayList<>();
        mock1.add("10");mock1.add("60"); mock1.add("9");
        List<Object> mock = new ArrayList<>();
        mock.add("ALLOW"); mock.add(mock1);

        RScript scriptMock = mock(RScript.class);
        when(redissonClientMock.getScript(StringCodec.INSTANCE)).thenReturn(scriptMock);
        Object[] params = new Object[] {10, 60};
        when(scriptMock.eval(anyString(), eq(RScript.Mode.READ_WRITE), any() , eq(RScript.ReturnType.MULTI), anyList(), eq(params)))
                .thenReturn(mock); // Adjust the values as needed
        Rate rate = new Rate();
        rate.setLimit(10); rate.setRefreshInterval(60); rate.setRefill(8); rate.setRemaining(9);
        Rate result = rateLimiterSpy.checkLimit(key, rate);

        assertNotNull(result);
        assertEquals(RateLimitStatus.ALLOW, result.getStatus());
        assertEquals(60, result.getRefreshInterval());
        assertEquals(9, result.getRemaining());

        verify(scriptMock).eval(anyString(),
                eq(RScript.Mode.READ_WRITE),
                anyString(),
                eq(RScript.ReturnType.MULTI),
                anyList(),
                eq(params));
    }

    @Test
    public void testTryConsumeException(){
        String key = "testKey";
        FixedWindowRateLimiter rateLimiterSpy = spy(rateLimiterTest);
        List<Object> mock1 = new ArrayList<>();
        mock1.add("10");mock1.add("60"); mock1.add("9");
        List<Object> mock = new ArrayList<>();
        mock.add("ALLOW"); mock.add(mock1);
        RScript scriptMock = mock(RScript.class);
        when(redissonClientMock.getScript(StringCodec.INSTANCE)).thenReturn(scriptMock);
        Object[] params = new Object[] {10, 60};
        when(scriptMock.eval(anyString(), eq(RScript.Mode.READ_WRITE), anyString() , eq(RScript.ReturnType.MULTI), anyList(), eq(params)))
                .thenReturn(null); // Adjust the values as needed
        Rate rate = new Rate();
        rate.setLimit(10); rate.setRefreshInterval(60); rate.setRefill(8); rate.setRemaining(9);
        RateProcessingException thrown = assertThrows(
                RateProcessingException.class,
                () -> rateLimiterSpy.checkLimit(key,rate),
                "Expected checkLimit() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Couldn't find the Rate in Redis"));

    }
}