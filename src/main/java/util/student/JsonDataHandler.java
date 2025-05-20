package util.student;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.student.Reservation;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// 학생 JSON 데이터를 처리하는 유틸리티 클래스
public class JsonDataHandler {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();
    
    private static final String BASE_PATH = "/Users/leeng/Desktop/2025_project/deu_class/";

    public static List<Reservation> loadReservations(String fileName) {
        try {
            File file = new File(BASE_PATH + fileName);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            
            try (Reader reader = new FileReader(file)) {
                Type type = new TypeToken<List<Reservation>>(){}.getType();
                List<Reservation> reservations = gson.fromJson(reader, type);
                return reservations != null ? reservations : new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean saveReservations(List<Reservation> reservations, String fileName) {
        try {
            File file = new File(BASE_PATH + fileName);
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(reservations, writer);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // LocalDateTime 직렬화/역직렬화 어댑터
    private static class LocalDateTimeAdapter implements com.google.gson.JsonSerializer<LocalDateTime>, com.google.gson.JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public LocalDateTime deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }

        @Override
        public com.google.gson.JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(src.format(formatter));
        }
    }
} 