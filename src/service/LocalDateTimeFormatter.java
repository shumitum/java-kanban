package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeFormatter {

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .setPrettyPrinting();
        return gsonBuilder.create();
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
        private final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime != null) {
                jsonWriter.value(localDateTime.format(formatterWriter));
            } else {
                jsonWriter.nullValue();
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            try {
                return LocalDateTime.parse(jsonReader.nextString(), formatterReader);
            } catch (IllegalStateException e) {
                jsonReader.nextNull();
            }
            return null;
        }
    }
}
