package com.intuit.ratelimiter.core;

import com.intuit.ratelimiter.constants.RateLimitStatus;
import com.intuit.ratelimiter.exception.FileLoadException;
import com.intuit.ratelimiter.exception.RateProcessingException;
import com.intuit.ratelimiter.model.RPolicy;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SlidingWindowRateLimiterTest {

    private SlidingWindowRateLimiter rateLimiterTest;
    private RateLimiterRedisConnection rateLimiterRedisConnectionMock;
    private RedissonClient redissonClientMock;

    public SlidingWindowRateLimiterTest() throws FileLoadException {
        rateLimiterRedisConnectionMock = mock(RateLimiterRedisConnection.class);
        redissonClientMock = mock(RedissonClient.class);
        when(rateLimiterRedisConnectionMock.getRedisClient()).thenReturn(redissonClientMock);
        rateLimiterTest = new SlidingWindowRateLimiter(rateLimiterRedisConnectionMock);
    }

    @Test
    public void testTryConsume(){
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

        RPolicy rPolicy = new RPolicy();
        rPolicy.setLimit(10); rPolicy.setRefreshInterval(60); rPolicy.setRefill(8);
        Rate result = rateLimiterSpy.checkLimit(key, rPolicy);

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
        SlidingWindowRateLimiter rateLimiterSpy = spy(rateLimiterTest);
        List<Object> mock1 = new ArrayList<>();
        mock1.add("10");mock1.add("60"); mock1.add("9");
        List<Object> mock = new ArrayList<>();
        mock.add("ALLOW"); mock.add(mock1);
        RScript scriptMock = mock(RScript.class);
        when(redissonClientMock.getScript(StringCodec.INSTANCE)).thenReturn(scriptMock);
        Object[] params = new Object[] {10, 60};
        when(scriptMock.eval(anyString(), eq(RScript.Mode.READ_WRITE), anyString() , eq(RScript.ReturnType.MULTI), anyList(), eq(params)))
                .thenReturn(null); // Adjust the values as needed
        RPolicy rPolicy = new RPolicy();
        rPolicy.setLimit(10); rPolicy.setRefreshInterval(60); rPolicy.setRefill(8);
        RateProcessingException thrown = assertThrows(
                RateProcessingException.class,
                () -> rateLimiterSpy.checkLimit(key, rPolicy),
                "Expected checkLimit() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Couldn't find the Rate in Redis"));

    }
}