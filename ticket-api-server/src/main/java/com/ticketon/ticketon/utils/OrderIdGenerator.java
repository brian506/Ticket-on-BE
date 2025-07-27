package com.ticketon.ticketon.config;


import de.huxhorn.sulky.ulid.ULID;

public class OrderIdGenerator {

    private static final ULID ulid = new ULID();

    public static String nextId() {
        return ulid.nextULID();
    }
}
