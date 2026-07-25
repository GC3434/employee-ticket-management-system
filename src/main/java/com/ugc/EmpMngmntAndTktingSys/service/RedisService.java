package com.ugc.EmpMngmntAndTktingSys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public void savedata(String key, String value){
        redisTemplate.opsForValue().set(key,value);
    }

    public String getdata(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key){
        redisTemplate.delete(key);
    }

}
