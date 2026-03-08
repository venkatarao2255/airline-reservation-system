package com.airline.api;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpUtil {
    public static String readBody(HttpExchange ex) throws IOException {
        try (InputStream in = ex.getRequestBody()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int r;
            while ((r = in.read(buf)) != -1) bos.write(buf, 0, r);
            return bos.toString(StandardCharsets.UTF_8);
        }
    }

    public static void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        Headers h = ex.getResponseHeaders();
        h.set("Content-Type", "application/json; charset=utf-8");
        setCors(h);
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    public static void sendEmpty(HttpExchange ex, int status) throws IOException {
        Headers h = ex.getResponseHeaders();
        setCors(h);
        ex.sendResponseHeaders(status, -1);
        ex.close();
    }

    public static void setCors(Headers h) {
        h.set("Access-Control-Allow-Origin", "http://localhost:5173");
        h.set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        h.set("Access-Control-Allow-Headers", "Content-Type,Authorization");
        h.set("Access-Control-Allow-Credentials", "true");
    }

    public static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> out = new LinkedHashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) return out;
        String[] parts = rawQuery.split("&");
        for (String part : parts) {
            int idx = part.indexOf('=');
            String k = idx >= 0 ? part.substring(0, idx) : part;
            String v = idx >= 0 ? part.substring(idx + 1) : "";
            k = urlDecode(k);
            v = urlDecode(v);
            if (!k.isEmpty()) out.put(k, v);
        }
        return out;
    }

    private static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }
}

