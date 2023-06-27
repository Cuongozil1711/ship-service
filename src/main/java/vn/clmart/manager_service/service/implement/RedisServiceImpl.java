package vn.clmart.manager_service.service.implement;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }

    @Override
    public void setValue(String key, Object value) {
//        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object getValue(String key) {
//        return redisTemplate.opsForValue().get(key);
          return null;
    }

    @Override
    public void deleteKey(String key) {
//        redisTemplate.delete(key);
    }
}
