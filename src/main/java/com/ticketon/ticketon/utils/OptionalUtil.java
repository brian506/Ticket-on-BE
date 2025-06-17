package com.ticketon.ticketon.utils;

import com.ticketon.ticketon.exception.custom.NotFoundDataException;

import java.util.Optional;

public class OptionalUtil {
    public static <T> T getOrElseThrow(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new NotFoundDataException(message));
    }
}
