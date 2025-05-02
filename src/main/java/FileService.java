package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileService {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static <T> List<T> readData(String filePath, Class<T> valueType) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
            mapper.writeValue(file, List.of());
        }
        return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, valueType));
    }

    public static <T> void writeData(String filePath, List<T> data) throws IOException {
        mapper.writeValue(new File(filePath), data);
    }
}