local id = KEYS[1]
local limit = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local task = ARGV[3]


local function headers(window, remaining)
  return {
    tostring(window),
    tostring(remaining)
  }
end

if redis.pcall('EXISTS', id) ~= 1 then
    if task == 'consume' then
        local h = headers(-1, -1)
        return {'key_miss', h}
    end
    if task == 'setrate' then
        local count = limit - 1
        redis.pcall('SETEX', id, window, count)
        local h = headers(window, count)
        return {'allow', h}
    end
end

if redis.pcall('EXISTS', id) == 1 then
    local count = redis.pcall('DECR', id)
    local ttl = redis.pcall('TTL', id)
    if count >= 0 then
        local h = headers( ttl, count)
        return {'allow', h}
    else
        local h = headers( ttl, 0)
        return {'deny', h}
    end
end

