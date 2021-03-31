local prevUpdateTime = tonumber(redis.call('get', KEYS[2]) or '0')
local t = redis.call('time')
local currentTime = t[1] + t[2]/1000000     --当前时间
local tokensPerSec = tonumber(ARGV[1])
local tokensLimit = tonumber(ARGV[2])
local tokens = (currentTime - prevUpdateTime) * tokensPerSec + tonumber(redis.call('get', KEYS[1]) or '0') --当前令牌数
local resultValue = tokens;
if(tokens > 0)  --是否有令牌
    then redis.call('set', KEYS[2], currentTime)    --更新令牌数量
        if(tokens > tokensLimit)
            then redis.call('set', KEYS[1],  tokensLimit - 1)
                resultValue = tokensLimit
            else redis.call('set', KEYS[1],  tokens - 1)
        end
    resultValue = resultValue - 1
    return resultValue
else
    return -1
end