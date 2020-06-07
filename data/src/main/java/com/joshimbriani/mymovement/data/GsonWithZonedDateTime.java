package com.joshimbriani.mymovement.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class GsonWithZonedDateTime {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, (JsonDeserializer<ZonedDateTime>) (json, typeOfT, context) -> {
            JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();

            try {
                if (jsonPrimitive.isString()) {
                    return ZonedDateTime.parse(jsonPrimitive.getAsString(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
                }

                // if provided as Long
                if(jsonPrimitive.isNumber()){
                    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(jsonPrimitive.getAsLong()), ZoneId.systemDefault());
                }
            } catch (RuntimeException e) {
                throw new JsonParseException("Unable to parse ZonedDateTime", e);
            }
            throw new JsonParseException("Unable to parse ZonedDateTime");
        }).registerTypeAdapter(ZonedDateTime.class, (JsonSerializer<ZonedDateTime>) (src, typeOfSrc, context) -> {
            DateTimeFormatter fmt = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            return new JsonPrimitive(fmt.format(src.truncatedTo(ChronoUnit.SECONDS)));
        }).create();
    }
}
