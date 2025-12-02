package edu.og.api.testCode.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

//import org.apache.tomcat.jni.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonFileReaderService {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonFileReaderService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${app.json.save.path:./json-files}")
    private String jsonSavePath;
    
    // 1. 파일 경로로 JSON 문자열 읽기 (가장 기본적인 방법)
    public String readJsonAsString(String fileName) {
        try {
            Path filePath = Paths.get(fileName);
            if (!Files.exists(filePath)) {
                logger.warn("파일이 존재하지 않습니다: {}", fileName);
                return null;
            }
            
            String jsonContent = Files.readString(filePath, StandardCharsets.UTF_8);
            logger.info("[JsonFileReaderService.readJsonAsString()]JSON 파일 읽기 완료: {}", fileName);
            return jsonContent;
            
        } catch (IOException e) {
            logger.error("파일 읽기 실패: {}", fileName, e);
            return null;
        }
    }
    
    // 2. 설정된 경로에서 JSON 파일 읽기
    public String readJsonFromConfiguredPath(String fileName) {
        try {
            Path directory = Paths.get(jsonSavePath);
            Path filePath = directory.resolve(fileName);
            
            if (!Files.exists(filePath)) {
                logger.warn("파일이 존재하지 않습니다: {}", filePath.toAbsolutePath());
                return null;
            }
            
            String jsonContent = Files.readString(filePath, StandardCharsets.UTF_8);
            logger.info("JSON 파일 읽기 완료: {}", filePath.toAbsolutePath());
            return jsonContent;
            
        } catch (IOException e) {
            logger.error("파일 읽기 실패: {}", fileName, e);
            return null;
        }
    }
    
    // 3. JSON 파일을 직접 객체로 읽기
    public <T> T readJsonAsObject(String fileName, Class<T> clazz) {
        try {
            Path filePath = Paths.get(jsonSavePath).resolve(fileName);
            
            if (!Files.exists(filePath)) {
                logger.warn("파일이 존재하지 않습니다: {}", filePath.toAbsolutePath());
                return null;
            }
            
            T result = objectMapper.readValue(filePath.toFile(), clazz);
            logger.info("JSON 객체 읽기 완료: {} -> {}", fileName, clazz.getSimpleName());
            return result;
            
        } catch (IOException e) {
            logger.error("JSON 객체 읽기 실패: {}", fileName, e);
            return null;
        }
    }
    
    // 4. JSON 파일을 TypeReference로 읽기 (제네릭 타입용)
    public <T> T readJsonAsObject(String fileName, TypeReference<T> typeReference) {
        try {
            Path filePath = Paths.get(jsonSavePath).resolve(fileName);
            
            if (!Files.exists(filePath)) {
                logger.warn("파일이 존재하지 않습니다: {}", filePath.toAbsolutePath());
                return null;
            }
            
            T result = objectMapper.readValue(filePath.toFile(), typeReference);
            logger.info("JSON 객체 읽기 완료: {}", fileName);
            return result;
            
        } catch (IOException e) {
            logger.error("JSON 객체 읽기 실패: {}", fileName, e);
            return null;
        }
    }
    
    // 5. 가장 최근 파일 찾아서 읽기 (타임스탬프 기반)
    public String readLatestJsonFile(String baseFileName) {
        try {
            Path directory = Paths.get(jsonSavePath);
            if (!Files.exists(directory)) {
                logger.warn("디렉토리가 존재하지 않습니다: {}", directory.toAbsolutePath());
                return null;
            }
            
            // baseFileName으로 시작하는 파일들 찾기
            Optional<Path> latestFile = Files.list(directory)
                .filter(path -> path.getFileName().toString().startsWith(baseFileName))
                .filter(Files::isRegularFile)
                .max(Comparator.comparing(path -> {
                    try {
                        return Files.getLastModifiedTime(path);
                    } catch (IOException e) {
                        return FileTime.fromMillis(0);
                    }
                }));
            
            if (latestFile.isPresent()) {
                String content = Files.readString(latestFile.get(), StandardCharsets.UTF_8);
                logger.info("최신 파일 읽기 완료: {}", latestFile.get().getFileName());
                return content;
            } else {
                logger.warn("조건에 맞는 파일을 찾을 수 없습니다: {}", baseFileName);
                return null;
            }
            
        } catch (IOException e) {
            logger.error("최신 파일 읽기 실패: {}", baseFileName, e);
            return null;
        }
    }
    
    // 6. 저장된 파일 목록 조회
    public List<String> listJsonFiles() {
        try {
            Path directory = Paths.get(jsonSavePath);
            if (!Files.exists(directory)) {
                logger.info("디렉토리가 존재하지 않습니다: {}", directory.toAbsolutePath());
                return Collections.emptyList();
            }
            
            return Files.list(directory)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> path.getFileName().toString())
                .sorted()
                .collect(Collectors.toList());
                
        } catch (IOException e) {
            logger.error("파일 목록 조회 실패", e);
            return Collections.emptyList();
        }
    }
    
    // 7. 파일 정보와 함께 목록 조회
    public List<FileInfo> listJsonFilesWithInfo() {
        try {
            Path directory = Paths.get(jsonSavePath);
            if (!Files.exists(directory)) {
                return Collections.emptyList();
            }
            
            return Files.list(directory)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> {
                    try {
                        return new FileInfo(
                            path.getFileName().toString(),
                            Files.size(path),
                            Files.getLastModifiedTime(path).toInstant()
                        );
                    } catch (IOException e) {
                        logger.error("파일 정보 읽기 실패: {}", path, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(FileInfo::getLastModified).reversed())
                .collect(Collectors.toList());
                
        } catch (IOException e) {
            logger.error("파일 정보 목록 조회 실패", e);
            return Collections.emptyList();
        }
    }
    
    // 파일 정보 클래스
    public static class FileInfo {
        private String fileName;
        private long size;
        private Instant lastModified;
        
        public FileInfo(String fileName, long size, Instant lastModified) {
            this.fileName = fileName;
            this.size = size;
            this.lastModified = lastModified;
        }
        
        // getters
        public String getFileName() { return fileName; }
        public long getSize() { return size; }
        public Instant getLastModified() { return lastModified; }
        public String getFormattedSize() {
            if (size < 1024) return size + " B";
            if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }
}