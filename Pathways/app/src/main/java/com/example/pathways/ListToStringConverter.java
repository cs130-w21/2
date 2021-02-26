package com.example.pathways;

import android.graphics.Bitmap;
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
    public static List<SpotifyActivity.SongInfo> stringToSongInfoList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<SpotifyActivity.SongInfo>>() {}.getType();

        return _gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String BitmapListToString(List<Bitmap> someObjects) {
        return _gson.toJson(someObjects);
    }

    @TypeConverter
    public static List<Bitmap> stringToBitmapList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Bitmap>>() {}.getType();

        return _gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String SongInfoListToString(List<SpotifyActivity.SongInfo> someObjects) {
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
