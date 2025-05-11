package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProfessorView {
    private Scanner scanner = new Scanner(System.in);

    public String getProfessorName() {
        System.out.print("교수 이름을 입력하세요: ");
        return scanner.nextLine();
    }

    public int getProfessorId() {
        System.out.print("교수 교번을 입력하세요: ");
        return scanner.nextInt();
    }
    public String chooseRoomType() {
        System.out.println("실습실과 일반실 중 선택하세요:");
        System.out.println("1. 실습실");
        System.out.println("2. 일반실");
        System.out.print("선택하세요 (1 또는 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // 버퍼 클리어
        return choice == 1 ? "실습실" : "일반실";  // 1이면 실습실, 2이면 일반실
    }

    public int chooseRoom(List<Integer> roomNumbers) {
        System.out.println("실습실 목록:");
        for (Integer room : roomNumbers) {
            System.out.println("- " + room);
        }
        System.out.print("사용할 실습실 번호를 선택하세요: ");
        return scanner.nextInt();
    }

    public String chooseDay(List<String> days) {
        System.out.println("요일 목록:");
        for (String day : days) {
            System.out.println("- " + day);
        }
        System.out.print("요일을 선택하세요: ");
        scanner.nextLine(); // 버퍼 클리어
        return scanner.nextLine();
    }

    public List<String> chooseTimeSlots(List<String> slots) {
        System.out.println("시간대 목록 (최대 3개 선택):");
        for (int i = 0; i < slots.size(); i++) {
            System.out.println((i + 1) + ". " + slots.get(i));
        }

        List<String> chosen = new ArrayList<>();
        while (chosen.size() < 3) {
            System.out.print("선택할 시간대 번호 (0 입력시 종료): ");
            int choice = scanner.nextInt();
            if (choice == 0) break;
            if (choice > 0 && choice <= slots.size()) {
                String time = slots.get(choice - 1);
                if (!chosen.contains(time)) chosen.add(time);
            }
        }
        return chosen;
    }

    public void displayReservation(model.RoomReservation reservation) {
        System.out.println("\n====== 예약 정보 ======");
        System.out.println("강의실 종류: " + reservation.getRoomType());
        System.out.println("요일: " + reservation.getDay());
        System.out.println("시간:");
        for (String time : reservation.getperiod()) {
            System.out.println("- " + time);
        }
    }
}
