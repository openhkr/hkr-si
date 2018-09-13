package com.reachauto.hkr.si.config;

import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/22.
 */
public class WXHttpMessageConverter extends MappingJackson2XmlHttpMessageConverter {

    public WXHttpMessageConverter() {
        this.setSupportedMediaTypes();
    }

    private final void setSupportedMediaTypes() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.ALL);
        super.setSupportedMediaTypes(mediaTypes);
    }
}
