package util;

import model.ta.Reservation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonDataHandler {
    private static final String STUDENT_RESERVATIONS_FILE = "deu_class/src/main/resources/student_reservations.json";
    private static final String STUDENT_APPROVED_RESERVATIONS_FILE = "deu_class/src/main/resources/student_approved_reservations.json";
    private static final String STUDENT_REJECTED_RESERVATIONS_FILE = "deu_class/src/main/resources/student_rejected_reservations.json";
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type listType = new TypeToken<ArrayList<Reservation>>(){}.getType();
    
    public List<Reservation> loadStudentReservations() {
        return loadFromFile(STUDENT_RESERVATIONS_FILE);
    }
    
    public List<Reservation> loadStudentApprovedReservations() {
        return loadFromFile(STUDENT_APPROVED_RESERVATIONS_FILE);
    }
    
    public List<Reservation> loadStudentRejectedReservations() {
        return loadFromFile(STUDENT_REJECTED_RESERVATIONS_FILE);
    }
    
    public void saveStudentReservations(List<Reservation> reservations) {
        saveToFile(STUDENT_RESERVATIONS_FILE, reservations);
    }
    
    public void saveStudentApprovedReservations(List<Reservation> reservations) {
        saveToFile(STUDENT_APPROVED_RESERVATIONS_FILE, reservations);
    }
    
    public void saveStudentRejectedReservations(List<Reservation> reservations) {
        saveToFile(STUDENT_REJECTED_RESERVATIONS_FILE, reservations);
    }
    
    private List<Reservation> loadFromFile(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                return new ArrayList<>();
            }
            
            try (Reader reader = new FileReader(file)) {
                List<Reservation> result = gson.fromJson(reader, listType);
                return result != null ? result : new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private void saveToFile(String filename, List<Reservation> reservations) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(reservations, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 