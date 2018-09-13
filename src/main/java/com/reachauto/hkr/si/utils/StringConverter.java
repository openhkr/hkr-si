package com.reachauto.hkr.si.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: xiangning
 * Date: 2017/9/21 22:12
 * To change this template use File | Settings | File Templates.
 * chenxiangning@reachauto.com
 */
class StringConverter extends TypeAdapter<String> {

    @Override
    public void write(JsonWriter jsonWriter, String value) throws IOException {
        if (value == null) {
            // 序列化时将 null 转为 ""
            jsonWriter.value("");
        } else {
            jsonWriter.value(value);
        }
    }

    @Override
    public String read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        String str = jsonReader.nextString();
        // 反序列化时将 "" 转为 null
        if ("".equals(str)) {
            return null;
        } else {
            return str;
        }
    }
}