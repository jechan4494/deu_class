package test.model;

import model.professor.ProfessorApprovedListModel;
import model.professor.ProfessorApprovedModel;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProfessorApprovedListModelTest {

    private static String jsonFilePath;
    private static List<ProfessorApprovedModel> sampleList;

    @BeforeAll
    static void setUp() throws Exception {
        sampleList = new ArrayList<>();
        sampleList.add(new ProfessorApprovedModel("교수", 911, "홍길동", Arrays.asList("09:00-10:00"), "승인", "월", "강의실"));
        sampleList.add(new ProfessorApprovedModel("조교", 912, "김철수", Arrays.asList("10:00-11:00"), "대기", "화", "실험실"));
        sampleList.add(new ProfessorApprovedModel("교수", 915, "홍길동", Arrays.asList("11:00-12:00"), "승인", "수", "강의실"));

        // 임시 파일에 JSON 형태로 저장
        File tempFile = File.createTempFile("test_professor_approved_", ".json");
        tempFile.deleteOnExit();

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(tempFile, sampleList);
        jsonFilePath = tempFile.getAbsolutePath();
    }

    @Test
    void loadAndGetList_테스트() throws Exception {
        ProfessorApprovedListModel model = new ProfessorApprovedListModel(jsonFilePath);
        List<ProfessorApprovedModel> list = model.getList();
        assertEquals(3, list.size());
        assertEquals("홍길동", list.get(0).getName());
        System.out.println("loadAndGetList_테스트 완료");
    }

    @Test
    void getMySchedules_테스트() throws Exception {
        ProfessorApprovedListModel model = new ProfessorApprovedListModel(jsonFilePath);
        List<ProfessorApprovedModel> mySchedules = model.getMySchedules("홍길동", "교수");
        assertEquals(2, mySchedules.size());
        assertTrue(mySchedules.stream().allMatch(s -> s.getName().equals("홍길동") && s.getRole().equals("교수")));
        System.out.println("getMySchedules_테스트 완료");
    }

    @Test
    void saveAndReload_테스트() throws Exception {
        ProfessorApprovedListModel model = new ProfessorApprovedListModel(jsonFilePath);
        String tempSavePath = Files.createTempFile("save_test_", ".json").toString();
        model.save(tempSavePath);

        ProfessorApprovedListModel model2 = new ProfessorApprovedListModel(tempSavePath);
        assertEquals(3, model2.getList().size());
        Files.deleteIfExists(Paths.get(tempSavePath));
        System.out.println("saveAndReload_테스트 완료");
    }
}