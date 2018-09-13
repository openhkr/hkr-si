package com.reachauto.hkr.si.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Created with IntelliJ IDEA.
 * User: xiangning
 * Date: 2017/9/21 21:28
 * To change this template use File | Settings | File Templates.
 * chenxiangning@reachauto.com
 */
class NullStringToEmptyAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (rawType != String.class) {
            return null;
        }
        return (TypeAdapter<T>) new StringNullAdapter();
    }
}