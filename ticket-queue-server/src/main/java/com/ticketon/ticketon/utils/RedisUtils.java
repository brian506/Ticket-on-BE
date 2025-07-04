package com.ticketon.ticketon.utils;

public class RedisUtils {

    private RedisUtils() {}

    public static String stripQuotesAndTrim(String value) {
        if (value == null) return null;
        return value.replace("\"", "").trim();
    }
}