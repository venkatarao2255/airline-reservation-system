package com.airline.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    public static Map<String, String> parseObject(String json) {
        if (json == null) return Map.of();
        String s = json.trim();
        if (s.isEmpty()) return Map.of();
        if (s.startsWith("\uFEFF")) s = s.substring(1).trim();
        if (!s.startsWith("{") || !s.endsWith("}")) return Map.of();
        int i = 1;
        Map<String, String> out = new LinkedHashMap<>();
        while (i < s.length() - 1) {
            i = skipWs(s, i);
            if (i >= s.length() - 1) break;
            if (s.charAt(i) == ',') {
                i++;
                continue;
            }
            if (s.charAt(i) == '}') break;
            String key;
            if (s.charAt(i) == '"') {
                ParseResult pr = parseString(s, i);
                key = pr.value;
                i = pr.next;
            } else {
                ParseResult pr = parseBare(s, i);
                key = pr.value;
                i = pr.next;
            }
            i = skipWs(s, i);
            if (i < s.length() && s.charAt(i) == ':') i++;
            i = skipWs(s, i);
            String val;
            if (i < s.length() && s.charAt(i) == '"') {
                ParseResult pr = parseString(s, i);
                val = pr.value;
                i = pr.next;
            } else {
                ParseResult pr = parseBare(s, i);
                val = pr.value;
                i = pr.next;
            }
            if (key != null && !key.isEmpty()) out.put(key, val == null ? "" : val);
            i = skipWs(s, i);
            if (i < s.length() && s.charAt(i) == ',') i++;
        }
        return out;
    }

    public static String obj(Map<String, ?> map) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean first = true;
        for (Map.Entry<String, ?> e : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(escape(e.getKey())).append('"').append(':');
            sb.append(value(e.getValue()));
        }
        sb.append('}');
        return sb.toString();
    }

    public static String arr(List<String> jsonObjects) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < jsonObjects.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(jsonObjects.get(i));
        }
        sb.append(']');
        return sb.toString();
    }

    public static String value(Object v) {
        if (v == null) return "null";
        if (v instanceof Number) return v.toString();
        if (v instanceof Boolean) return ((Boolean) v) ? "true" : "false";
        if (v instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, ?> m = (Map<String, ?>) v;
            return obj(m);
        }
        if (v instanceof List<?>) {
            List<?> list = (List<?>) v;
            List<String> items = new ArrayList<>();
            for (Object o : list) items.add(value(o));
            return "[" + String.join(",", items) + "]";
        }
        return "\"" + escape(String.valueOf(v)) + "\"";
    }

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 32) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.toString();
    }

    private static int skipWs(String s, int i) {
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == ' ' || c == '\n' || c == '\r' || c == '\t') i++;
            else break;
        }
        return i;
    }

    private static ParseResult parseString(String s, int start) {
        int i = start;
        if (s.charAt(i) != '"') return new ParseResult("", i);
        i++;
        StringBuilder sb = new StringBuilder();
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '"') {
                i++;
                break;
            }
            if (c == '\\') {
                i++;
                if (i >= s.length()) break;
                char esc = s.charAt(i);
                switch (esc) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        if (i + 4 < s.length()) {
                            String hex = s.substring(i + 1, i + 5);
                            try {
                                sb.append((char) Integer.parseInt(hex, 16));
                            } catch (NumberFormatException ignored) {
                            }
                            i += 4;
                        }
                        break;
                    default: sb.append(esc);
                }
                i++;
                continue;
            }
            sb.append(c);
            i++;
        }
        return new ParseResult(sb.toString(), i);
    }

    private static ParseResult parseBare(String s, int start) {
        int i = start;
        StringBuilder sb = new StringBuilder();
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == ',' || c == '}' || c == '\n' || c == '\r' || c == '\t' || c == ' ') break;
            sb.append(c);
            i++;
        }
        String v = sb.toString().trim();
        if ("null".equals(v)) v = "";
        return new ParseResult(v, i);
    }

    private static class ParseResult {
        final String value;
        final int next;

        ParseResult(String value, int next) {
            this.value = value;
            this.next = next;
        }
    }
}

