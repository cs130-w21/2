package com.example.pathways;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ListToStringConverter {
    //found online at https://medium.com/@toddcookevt/android-room-storing-lists-of-objects-766cca57e3f9
    private static Gson _gson = new Gson();

    @TypeConverter
    public static List<Long> stringToLongList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Long>>() {}.getType();

        return _gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String LongListToString(List<Long> someObjects) {
        return _gson.toJson(someObjects);
    }

    @TypeConverter
    public static List<String> stringToStringList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {}.getType();

        return _gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String StringListToString(List<String> someObjects) {
        return _gson.toJson(someObjects);
    }
}
