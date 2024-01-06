-- get current time in seconds
local now = tonumber(redis.call('TIME')[1])

local function headers(limit, window, remaining)
  return {
    tostring(limit),
    tostring(window),
    tostring(remaining)
  }
end

local bucketKey = KEYS[1]

-- Maximum tokens in the bucket
local maxTokens = tonumber(ARGV[1])

-- Tokens to add per request
local tokensToAdd = tonumber(ARGV[2])

-- Current timestamp in milliseconds
local currentTime = tonumber(ARGV[3])

-- Get the current token count and last refill time
local currentTokens = tonumber(redis.call('HGET', bucketKey, 'tokens')) or maxTokens
local lastRefillTime = tonumber(redis.call('HGET', bucketKey, 'lastRefillTime')) or currentTime

-- Calculate the time elapsed since the last refill
local timeElapsed = currentTime - lastRefillTime

-- Calculate the tokens to add based on the time elapsed
local tokensToAdd = math.floor(timeElapsed / (1000 / tokensToAdd))

-- Update the token count and last refill time
local newTokens = math.min(currentTokens + tokensToAdd, maxTokens)
redis.call('HSET', bucketKey, 'tokens', newTokens)
redis.call('HSET', bucketKey, 'lastRefillTime', currentTime)

-- Check if there are enough tokens for the operation
if newTokens >= 1 then
    return 1  -- Enough tokens, allow the operation
else
    return 0  -- Not enough tokens, deny the operation
end