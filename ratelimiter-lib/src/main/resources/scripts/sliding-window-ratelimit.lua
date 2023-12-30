local current_time = redis.call('TIME')
local trim_time = tonumber(current_time[1]) - ARGV[2]

local function headers(limit, window, remaining)
  return {
    tostring(limit),
    tostring(window),
    tostring(remaining)
  }
end

redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, trim_time)
local request_count = redis.call('ZCARD',KEYS[1])
local ttl = redis.pcall('TTL', KEYS[1])
if request_count < tonumber(ARGV[1]) then
    redis.call('ZADD', KEYS[1], current_time[1], current_time[1] .. current_time[2])
    redis.call('EXPIRE', KEYS[1], ARGV[2])
    local ttl = redis.pcall('TTL', KEYS[1])
    local h = headers(tonumber(ARGV[1]), ttl, request_count)
    return {'allow', h}
end
local h = headers(tonumber(ARGV[1]), ttl, 0)
return {'deny', h}