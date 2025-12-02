package edu.og.api.dbApiExhibition.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.og.api.dbApiExhibition.model.dto.ExhibitionApi2;
import edu.og.api.dbApiExhibition.model.dto.ExhibitionApiResponse2;
import edu.og.api.dbApiExhibition.model.exception.ApiRetrievalException;
import edu.og.api.testCode.controller.JsonFileWriterService;
import lombok.extern.slf4j.Slf4j;



@RequestMapping("/dbApiExhibition")
@Controller
@Slf4j
public class DbApiExhibitionController {

	@Value("${api.kcisa.serviceKey}")
	private String serviceKey;
	
	private String baseUrl = "https://api.kcisa.kr/openapi/API_CCA_145/request";
	
	@GetMapping("/retrieval")
	public String mainPage(
			@RequestParam(name="query", required=false, defaultValue="예술의 전당") String query,
			Model model
			) throws IOException {
		
		// Server(Java)에서 공공데이터 요청하기 
		String returnType = "json"; // currently placeholder

		

		// 변수 for file saving	
		ClassPathResource resource = new ClassPathResource("data/json");
		String jsonSavePath = resource.getFile().getAbsolutePath();
		
		String originalFileName = "dbApiExhibition.json";
		
		// 
		int initialNumOfRows = 10;
		//int initialPageNo = 4;
		int initialPageNo = 1; // ==>total ~ 7630 items collected on 20205/09/29
		
		// 전체 응답을 ExhibitionApiResponse2로 파싱
		ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper문자열 쉽게 다루게 해 줌	
		// 페이지당 조회해온 모든 items들을 모으기 위함 
		List<ExhibitionApi2> resultItemsAll = new ArrayList<>();
		int pages2Retrieve = 30;  // Number of pages to retrieve
		
		// 한페이지당 N(10)개씩 해서 30pages(300개 items) 받아오기: i가 페이지 넘버로 한페이지씩 증가 하도록
		for (int i=0; i<pages2Retrieve; i++) {
			int numOfRows = initialNumOfRows;
			int pageNo = initialPageNo + i; // 1 ~ 10 페이지까지, 각 페이지당 10개 items 담는다.
			
			
			@SuppressWarnings("deprecation") // The method fromHttpUrl(String) from the type UriComponentsBuilder is deprecated since version 6.2
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
			.queryParam("serviceKey", serviceKey)
			.queryParam("numOfRows", numOfRows)
			.queryParam("pageNo", pageNo);
			// --> 자동으로 url + 쿼리스트링이 합쳐진 주소가 생성
			
			String uriString = uriBuilder.build().toUriString();
			//log.debug("uriString : {}", uriString);
			
			// 요청 헤더
			HttpHeaders headers = new HttpHeaders();  // POST 방식 요청헤더:fetch(주소, {method:post, headers: {}, body }
			headers.set("Content-Type",  "application/json");
			//headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Accept",  "application/json");  // restTemplate.exchange()의 반환값을 JSON으로 받겠다는 세팅 
			//headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			
			// HTTP 요청 헤더와 바디를 묶어주는 객체
			HttpEntity<String> entity = new HttpEntity<>(headers);		
			
			// RestTemplate
			// - Spring 제공 HTTP클라이언트
			// - Spring 에서 외부로 HTTP 요청을 보내고 응답을 받는 역할의 객체
			RestTemplate restTemplate = new RestTemplate(); 

			try { 
				ResponseEntity<String> response  = restTemplate.exchange(  
						uriString, // 요청주소 + 쿼리스트링
						HttpMethod.GET, // GET 방식 요청
						entity, // HTTP 헤더, 바디
						String.class // 응답 데이터의 형태
						); 
				
				
				String responseBody = response.getBody();	
				//log.debug("responseBody : {}", responseBody); // -> 얻어온 데이터가 JSON이므로 이제 이거 다뤄야함 {"K": {"K":{}}
				
				// ----------------------------------------------
				// Jackson 라이브러리
				// -> Java에서 JSON을 다룰 수 있게 해주는 라이브러리
				
				// Jackson data-bind 라이브러리
				// -> JSON 데이터를 Java 객체(Map, DTO)로 변환할 수 있는 라이브러리
				//
				// spring-starter-web dependency에 Jackson data-bind 포함되어 있어 그냥 쓸 수 있다.
				
				//// 전체 응답을 ExhibitionApiResponse2로 파싱
				//ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper문자열 쉽게 다루게 해 줌			
				
				ExhibitionApiResponse2 apiResponse = objectMapper.readValue(responseBody, ExhibitionApiResponse2.class);
				List<ExhibitionApi2> items = apiResponse.getResponse().getBody().getItems().getItem();			
				//int totalCount = apiResponse.getResponse().getBody().getTotalCount();		// not needed here
				//log.debug("numOfRows: {}, pageNo: {}, totalCount: {}", numOfRows, pageNo, totalCount );
				
				log.debug("example retrieved items : {}", items);
				
				
				
				if (items == null) {
					log.debug("there is no items retrieved at pageNo ={}", pageNo);
					// 현재 까지 모은 items들(resultItemsAll)만 일단 저장하고, 사용자정의 ApiRetrievalException()오류 던진다.
					
		            // 최종 리스트를 Map으로 감싸기
		            Map<String, List<ExhibitionApi2>> resultMap = new HashMap<>();
		            resultMap.put("itemsList", resultItemsAll);

		            // JSON 문자열로 직렬화
		            String resultJsonString = objectMapper
		                    .writerWithDefaultPrettyPrinter()
		                    .writeValueAsString(resultMap);

		            // 출력 
		            log.debug("collected itmesList at this point : {}", resultJsonString);
		            //System.out.println(resultJsonString);			
		            
		    		// 저장 ---------------------------------------------------
		    		// file saving
		    		// 1. 현재 날짜+시간까지 구하기
		    		LocalDateTime now = LocalDateTime.now();
		    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		    		String formattedDateTime = now.format(formatter);
		    		
		    		// 2. 파일 이름 분리 (확장자 처리)
		    		int dotIndex = originalFileName.lastIndexOf(".");
		    		String namePart = originalFileName.substring(0, dotIndex);   // "dbApiExhibition"
		    		String extensionPart = originalFileName.substring(dotIndex); // ".json"
		    		
		    		// 3. 날짜를 파일 이름에 추가
		    		//String fileNameWithDate = namePart + "_" + formattedDate + extensionPart; // "dbApiExhibition_20250929.json"
		    		// 3. 날짜+시간을 파일 이름에 추가
		    		String fileNameWithDate = namePart + "_" + formattedDateTime + extensionPart; // "dbApiExhibition_20250929_082333.json"
		    		
		    		// 4. 전체 경로 만들기
		    		Path directory = Paths.get(jsonSavePath.replace("/bin/main", "/src/main/resources")); 
		    		Path filePath = directory.resolve(fileNameWithDate);			
		    		String filePathString = filePath.toString();
		    		
		    		JsonFileWriterService jsonFileService = new JsonFileWriterService();
		    		jsonFileService.saveJsonToFileByBufferedWriter(resultJsonString, filePathString);
		            
					
					// 사용자 정의 오류 출력
					throw new ApiRetrievalException(); // 
					//items = new ArrayList<>();  // 예외처리 monkey-patch
				}
				
                // 전체 리스트에 추가
                //if (items != null) resultItemsAll.addAll(items);
            
				resultItemsAll.addAll(items); // add all elements of items
							
				log.debug("i= {}, pageNo: {}, current retrieved data Count: {}", i, pageNo, numOfRows*i );
				
				
				
			} catch (Exception err) {
				err.printStackTrace();
			}
			
		} // end-of-for문
		
        // 최종 리스트를 Map으로 감싸기
        Map<String, List<ExhibitionApi2>> resultMap = new HashMap<>();
        resultMap.put("itemsList", resultItemsAll);

        // JSON 문자열로 직렬화
        String resultJsonString;
		try {
			resultJsonString = objectMapper
			        .writerWithDefaultPrettyPrinter()
			        .writeValueAsString(resultMap);
			
			// 출력 
			//log.debug("collected itmesList at this point : {}", resultJsonString);
			
			// ---------------------------------------------------
			// file saving
			// 1. 현재 날짜+시간까지 구하기
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
			String formattedDateTime = now.format(formatter);
			
			// 2. 파일 이름 분리 (확장자 처리)
			int dotIndex = originalFileName.lastIndexOf(".");
			String namePart = originalFileName.substring(0, dotIndex);   // "dbApiExhibition"
			String extensionPart = originalFileName.substring(dotIndex); // ".json"
			
			// 3. 날짜를 파일 이름에 추가
			//String fileNameWithDate = namePart + "_" + formattedDate + extensionPart; // "dbApiExhibition_20250929.json"
			// 3. 날짜+시간을 파일 이름에 추가
			String fileNameWithDate = namePart + "_" + formattedDateTime + extensionPart; // "dbApiExhibition_20250929_082333.json"
			
			// 4. 전체 경로 만들기
			Path directory = Paths.get(jsonSavePath.replace("/bin/main", "/src/main/resources")); 
			Path filePath = directory.resolve(fileNameWithDate);			
			String filePathString = filePath.toString();
			
			JsonFileWriterService jsonFileService = new JsonFileWriterService();
			jsonFileService.saveJsonToFileByBufferedWriter(resultJsonString, filePathString);
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// classpath:templates/    main   .html    ->  forward해준다
		//return "main";  // 화면 만들기는 main으로 포워드
		return "/testCode/testCode";  // testCode로 포워드
		//return "/exhibition/exhibitionList";  // 테스트마친후, /exhibition/exhibitionList 로 포워드
		
	}		


}
