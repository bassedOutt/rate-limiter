local limits = cjson.decode(ARGV[1])
local currentTime = tonumber(ARGV[2])
local longestDuration = limits[1][1] or 0
local savedKeys = {}

for i, limit in ipairs(limits) do
    local duration = limit[1]
    longestDuration = math.max(longestDuration, duration)
    local precision = limit[3] or duration
    precision = math.min(precision, duration)
    local blocks = math.ceil(duration / precision)
    local saved = {}
    table.insert(savedKeys, saved)
    saved.blockId = math.floor(currentTime / precision)
    saved.trimBefore = saved.blockId - blocks + 1

    saved.countKey = duration .. ':' .. precision .. ':'
    saved.tsKey = saved.countKey .. 'o'
    for j, key in ipairs(KEYS) do
        local oldTimestamp = redis.call('HGET', key, saved.tsKey)
        oldTimestamp = oldTimestamp and tonumber(oldTimestamp) or saved.trimBefore
        if oldTimestamp > currentTime then
            -- Don't write in the past
            return '1'
        end
        local decrement = 0
        local toDelete = {}
        local trim = math.min(saved.trimBefore, oldTimestamp + blocks)
        for oldBlock = oldTimestamp, trim - 1 do
            local blockKey = saved.countKey .. oldBlock
            local blockCount = redis.call('HGET', key, blockKey)
            if blockCount then
                decrement = decrement + tonumber(blockCount)
                table.insert(toDelete, blockKey)
            end
        end
        local currentCount
        if #toDelete > 0 then
            redis.call('HDEL', key, unpack(toDelete))
            currentCount = redis.call('HINCRBY', key, saved.countKey, -decrement)
        else
            currentCount = redis.call('HGET', key, saved.countKey)
        end
        if tonumber(currentCount or '0') >= limit[2] then
            return '1'
        end
    end
end

for i, limit in ipairs(limits) do
    local saved = savedKeys[i]
    for j, key in ipairs(KEYS) do
        redis.call('HSET', key, saved.tsKey, saved.trimBefore)
        redis.call('HINCRBY', key, saved.countKey, 1)
        redis.call('HINCRBY', key, saved.countKey .. saved.blockId, 1)
    end
end

if longestDuration > 0 then
    for _, key in ipairs(KEYS) do
        redis.call('EXPIRE', key, longestDuration)
    end
end

return '0'
