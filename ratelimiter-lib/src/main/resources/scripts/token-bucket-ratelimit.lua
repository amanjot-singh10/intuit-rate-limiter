

local function headers(limit, window, remaining)
  return {
    tostring(limit),
    tostring(window),
    tostring(remaining)
  }
end

local bucketKey = KEYS[1]  -- Key to represent the token bucket
local capacity = tonumber(ARGV[1])  -- Maximum tokens the bucket can hold
local refillTokens = tonumber(ARGV[2])  -- Tokens to add per refill
local refillPeriod = tonumber(ARGV[3])  -- Refill period in seconds

-- get current time in seconds
local currentTime = tonumber(redis.call('TIME')[1])
-- Get the current token count and last refill time
local currentTokens = tonumber(redis.call('HGET', bucketKey, 'tokens')) or 0
local lastRefillTime = tonumber(redis.call('HGET', bucketKey, 'lastRefillTime')) or 0

-- Calculate the time elapsed since the last refill
local timeElapsed = currentTime - lastRefillTime

-- Calculate the tokens to add based on the time elapsed
local tokensToAdd = math.floor(timeElapsed / refillPeriod) * refillTokens

-- Update the token count and last refill time
local newTokens = math.min(currentTokens + tokensToAdd, capacity)

if tokensToAdd > 0 then
    redis.call('HSET', bucketKey, 'lastRefillTime', currentTime)
end

-- Check if there are enough tokens for the operation
if newTokens >= 1 then
    redis.call('HSET', bucketKey, 'tokens', newTokens-1)
    local h = headers(capacity, refillPeriod, newTokens-1)
    return {'allow', h}
else
    local h = headers(capacity, refillPeriod, newTokens)
    return {'deny', h}
end