package com.motilaloswal.services;

import com.motilaloswal.dto.cfFullResponse.CfFullDataResponse;
import org.springframework.core.ParameterizedTypeReference;

public interface RedisService {
    <T> T get(String key, ParameterizedTypeReference<T> aemResponseClass);

    void set(String key, Object o, Long ttl);
}
