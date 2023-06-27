package vn.clmart.manager_service.service;

public interface RedisService {
    void setValue(String key, Object value);
    Object getValue(String key);
    void deleteKey(String key);
}
