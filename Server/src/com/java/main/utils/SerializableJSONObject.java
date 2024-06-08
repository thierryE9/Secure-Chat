package com.java.main.utils;

import org.json.JSONObject;

import java.io.Serializable;

public class SerializableJSONObject extends JSONObject implements Serializable {

    public SerializableJSONObject(String json) {
        super(json);
    }
}