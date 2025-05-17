/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.ta;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReservationModel {
    private static final String RESERVATIONS_PATH = "deu_class/src/main/resources/reservations.json";
    private static final String APPROVED_RESERVATIONS_PATH = "deu_class/src/main/resources/approved_reservations.json";
    private static final String REJECTED_RESERVATIONS_PATH = "deu_class/src/main/resources/rejected_reservations.json";
    private final Gson gson;
    private static final Type RESERVATION_LIST_TYPE = new TypeToken<List<Reservation>>(){}.getType();

    public ReservationModel() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<Reservation> loadReservedReservations() {
        return readFromReservationJson("대기");
    }

    public List<Reservation> readFromReservationJson(String targetState) {
        List<Reservation> list = new ArrayList<>();
        File file = new File(RESERVATIONS_PATH);

        try (FileReader reader = new FileReader(file)) {
            List<Reservation> reservations = gson.fromJson(reader, RESERVATION_LIST_TYPE);
            
            if (reservations != null) {
                for (Reservation r : reservations) {
                    if (r.getState().equals(targetState)) {
                        list.add(r);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Reservation> loadApprovedReservations() {
        List<Reservation> list = new ArrayList<>();
        File file = new File(APPROVED_RESERVATIONS_PATH);

        try (FileReader reader = new FileReader(file)) {
            List<Reservation> reservations = gson.fromJson(reader, RESERVATION_LIST_TYPE);
            
            if (reservations != null) {
                list.addAll(reservations);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Reservation> loadRejectedReservations() {
        List<Reservation> list = new ArrayList<>();
        File file = new File(REJECTED_RESERVATIONS_PATH);

        try (FileReader reader = new FileReader(file)) {
            List<Reservation> reservations = gson.fromJson(reader, RESERVATION_LIST_TYPE);
            
            if (reservations != null) {
                list.addAll(reservations);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("✅ rejected_reservations.json 항목 수: " + list.size());
        return list;
    }
}
