package com.ragesh.rageshmart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.DEFAULT)
            .create();

    private JsonUtil() { }

    public static void writeJson(HttpServletResponse resp, int statusCode, Object body) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(GSON.toJson(body));
    }

    public static <T> T readJson(HttpServletRequest req, Class<T> clazz) throws IOException {
        try (Reader reader = req.getReader()) {
            return GSON.fromJson(reader, clazz);
        }
    }
}
