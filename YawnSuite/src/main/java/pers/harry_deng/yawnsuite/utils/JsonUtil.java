package pers.harry_deng.yawnsuite.utils;

import com.google.gson.*;

public class JsonUtil {

    public static String toJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }

    public static Object toObject(String fd, Class<?> C) {
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(fd).getAsJsonArray();
        Gson gson = new Gson();
        Object object = null;
        for (JsonElement item : jsonArray) {
            object = gson.fromJson(item, C);
        }
        return object;
    }

}
