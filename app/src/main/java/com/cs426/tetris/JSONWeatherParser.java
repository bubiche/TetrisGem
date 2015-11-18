package com.cs426.tetris;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONWeatherParser {
    public static double getTemp(String data) throws JSONException {
        double res = 0;

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);
        JSONObject mainObj = getObject("main", jObj);
        res = getFloat("temp", mainObj);

        return res;
    }

    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }
}
