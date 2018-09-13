package com.reachauto.hkr.si.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xiangning on 2017/1/30.
 */
class DateDeserializer implements JsonDeserializer<Date> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateDeserializer.class);

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

        try {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        } catch (NumberFormatException ne) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
            try {
                return sdf.parse(json.getAsJsonPrimitive().getAsString());
            } catch (ParseException | JsonParseException e) {
                LOGGER.error("{}", e);
                return null;
            }
        }
    }
}
