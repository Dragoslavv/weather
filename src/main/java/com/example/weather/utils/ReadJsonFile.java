package com.example.weather.utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReadJsonFile {
    private static final class JsonIterator<T> implements Iterator<T> {
        private final Gson gson;
        private final Type objectType;
        private final JsonReader reader;

        private JsonIterator(JsonReader reader, Gson gson, Type objectType) {
            this.gson = gson;
            this.objectType = objectType;
            this.reader = reader;
        }

        @Override
        public boolean hasNext() {
            try {
                return reader.hasNext();
            } catch (IOException ioe) {
                return false;
            }
        }

        @Override
        public T next() {
            return gson.fromJson(reader, objectType);
        }
    }

    public static <J> Stream<J> readJsonFromFile(Gson gson, URL jsonFile, Type type) throws IOException {
        JsonReader reader = new JsonReader(
                new BufferedReader(new InputStreamReader(jsonFile.openStream())));
        reader.beginArray();
        if (!reader.hasNext()) {
            return Stream.empty();
        }
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                new JsonIterator<J>(reader, gson, type), 0), false).onClose(() -> {
            try {
                reader.close();
            } catch (IOException ioe) {
            }
        });
    }
}
