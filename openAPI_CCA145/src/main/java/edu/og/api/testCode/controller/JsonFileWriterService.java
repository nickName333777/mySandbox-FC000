package edu.og.api.testCode.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonFileWriterService {

    // 방법 1: Files.write() 사용 (가장 간단)
    public void saveJsonToFileByFilesWrite(String jsonString, String fileName) {
        try {
            Path path = Paths.get(fileName);
            Files.write(path, jsonString.getBytes("UTF-8"));
            System.out.println("JSON 파일 저장 완료: " + fileName);
        } catch (IOException e) {
            System.err.println("파일 저장 실패: " + e.getMessage());
        }
    }

    // 방법 2: FileWriter 사용
    public void saveJsonToFielByFileWriter(String jsonString, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            writer.write(jsonString);
            writer.flush();
            System.out.println("JSON 파일 저장 완료: " + fileName);
        } catch (IOException e) {
            System.err.println("파일 저장 실패: " + e.getMessage());
        }
    }

    // 방법 3: BufferedWriter 사용 (큰 파일에 적합)
    public void saveJsonToFileByBufferedWriter(String jsonString, String fileName) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(fileName), 
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            
            writer.write(jsonString);
            writer.flush();
            System.out.println("JSON 파일 저장 완료: " + fileName);
        } catch (IOException e) {
            System.err.println("파일 저장 실패: " + e.getMessage());
        }
    }

    // 방법 4: 타임스탬프가 포함된 파일명으로 저장
    public String saveJsonWithTimestamp(String jsonString, String baseName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = baseName + "_" + timestamp + ".json";
            
            Path path = Paths.get(fileName);
            Files.write(path, jsonString.getBytes(StandardCharsets.UTF_8));
            
            System.out.println("JSON 파일 저장 완료: " + fileName);
            return fileName;
        } catch (IOException e) {
            System.err.println("파일 저장 실패: " + e.getMessage());
            return null;
        }
    }

    // 방법 5: 특정 디렉토리에 저장 (디렉토리가 없으면 생성)
    public void saveJsonToDirectory(String jsonString, String directory, String fileName) {
        try {
            // 디렉토리 생성 (없으면)
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // 파일 경로 생성
            Path filePath = dirPath.resolve(fileName);
            
            // 파일 저장
            Files.write(filePath, jsonString.getBytes(StandardCharsets.UTF_8));
            
            System.out.println("JSON 파일 저장 완료: " + filePath.toString());
        } catch (IOException e) {
            System.err.println("파일 저장 실패: " + e.getMessage());
        }
    }

    // 방법 6: JSON 포맷팅해서 저장 (ObjectMapper 사용)
    public void saveFormattedJson(String jsonString, String fileName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            
            // JSON 문자열을 파싱해서 포맷팅
            Object jsonObject = objectMapper.readValue(jsonString, Object.class);
            String formattedJson = objectMapper.writerWithDefaultPrettyPrinter()
                                              .writeValueAsString(jsonObject);
            
            // 파일로 저장
            Path path = Paths.get(fileName);
            Files.write(path, formattedJson.getBytes(StandardCharsets.UTF_8));
            
            System.out.println("포맷된 JSON 파일 저장 완료: " + fileName);
        } catch (IOException e) {
            System.err.println("파일 저장 실패: " + e.getMessage());
        }
    }

    // 방법 7: 리소스 폴더에 저장 (Spring Boot)
    @Value("${app.json.save.path:./json-files}")
    private String jsonSavePath;
    
    public void saveJsonToResourcePath(String jsonString, String fileName) {
        try {
            Path directory = Paths.get(jsonSavePath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            Path filePath = directory.resolve(fileName);
            Files.write(filePath, jsonString.getBytes(StandardCharsets.UTF_8));
            
            System.out.println("JSON 파일 저장 완료: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("파일 저장 실패: " + e.getMessage());
        }
    }
}