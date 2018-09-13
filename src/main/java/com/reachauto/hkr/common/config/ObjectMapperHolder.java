package com.reachauto.hkr.common.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperHolder {
    private static ObjectMapperHolder instance = new ObjectMapperHolder();
    private ObjectMapper mapper = createMapper();

    private ObjectMapperHolder() {
    }

    public static ObjectMapperHolder getInstance() {
        return instance;
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }

    public ObjectMapper getNewMapper() {
        return createMapper();
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        return mapper;
    }
}
