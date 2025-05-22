package model.student;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class StudentApprovedListModel {
    private final List<StudentApprovedModel> schedules;

    public StudentApprovedListModel(String jsonFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        schedules = mapper.readValue(
            new File(jsonFilePath),
            new TypeReference<>() {
            }
        );
    }

    // 전체 예약 목록 반환
    public List<StudentApprovedModel> getList() {
        return schedules;
    }

    // 파일로 저장
    public void save(String jsonFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(
            new File(jsonFilePath), schedules
        );
    }

    // 사용자의 이름, 역할로 필터링
    public List<StudentApprovedModel> getMySchedules(String name, String role) {
        return schedules.stream()
                .filter(s -> s.getName().equals(name) && s.getRole().equals(role))
                .collect(Collectors.toList());
    }
}