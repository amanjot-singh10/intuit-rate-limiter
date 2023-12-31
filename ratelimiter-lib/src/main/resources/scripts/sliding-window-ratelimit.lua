local current_time = redis.call('TIME')
local trim_time = tonumber(current_time[1]) - ARGV[2]
local task = ARGV[3]

local function headers(window, remaining)
  return {
    tostring(window),
    tostring(remaining)
  }
end

if redis.pcall('EXISTS', KEYS[1]) ~= 1 then
    if task == 'consume' then
        local h = headers(-1, -1)
        return {'key_miss', h}
    end
end

redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, trim_time)
local request_count = redis.call('ZCARD',KEYS[1])
local ttl = redis.pcall('TTL', KEYS[1])
if request_count < tonumber(ARGV[1]) then
    redis.call('ZADD', KEYS[1], current_time[1], current_time[1] .. current_time[2])
    redis.call('EXPIRE', KEYS[1], ARGV[2])
    local ttl = redis.pcall('TTL', KEYS[1])
    local h = headers(ttl, tonumber(ARGV[1])-request_count-1)
    return {'allow', h}
end
local h = headers(ttl, 0)
return {'deny', h}

