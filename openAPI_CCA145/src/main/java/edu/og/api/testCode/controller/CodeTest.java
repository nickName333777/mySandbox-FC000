package edu.og.api.testCode.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.og.api.dbApiExhibition.model.dto.ExhibitionApi2;
import edu.og.api.dbApiExhibition.model.dto.ExhibitionApiResponse2;
import edu.og.api.exhibition.model.dto.BoardImageOldOld;
import edu.og.api.exhibition.model.dto.ExhibitionOldOld;
import lombok.extern.slf4j.Slf4j;

//@PropertySource("classpath:/config.properties") // for json-file location (save&read) in config.properties
@Slf4j
@Controller
@RequestMapping("/sandbox")   
public class CodeTest {
	
    
	@GetMapping("/code1")  // JSON 문자열을 자바 객체로 바꿔주는거 테스트 (초기 버전:
	public String codeTest1(
			Model model			
			) {

	        // read example json file
			JsonFileReaderService jsonFileReaderService = new JsonFileReaderService();
			// 문자열로 읽기
			String json = jsonFileReaderService.readJsonAsString("dbApiExhibition_test1.json");

//			// 객체로 직접 읽기
//			ExhibitionApiResponse2 response = jsonFileReaderService.readJsonAsObject("dbApiExhibition_test1.json", ExhibitionApiResponse2.class);
	        
	        
	        try {
	            ObjectMapper mapper = new ObjectMapper();
	           
	            // 방법1) Map 과 Object(ArrayList)로 json 파싱하기
//	            Map<String, Object> map = mapper.readValue(json, Map.class);
//	            // 데이터 접근
//	            Map response = (Map) map.get("response");
//	            Map body = (Map) response.get("body");
//	            Map items = (Map) body.get("items");   
//	            Object itemList = items.get("item");
//
//	            //Map numOfRows = (Map) body.get("numOfRows"); // class java.lang.String cannot be cast to class java.lang.Map
//	            //int numOfRows = (int) body.get("numOfRows"); // class java.lang.String cannot be cast to class java.lang.Integer
//	            Object numOfRows =  body.get("numOfRows"); // Integer.parseInt(), Integer.valueOf()
//	            Object pageNo =  body.get("pageNo");
//	            Object totalCount =  body.get("totalCount");	  

	            // 방법2) wrapper class (ExhibitionApiResponse.java)로 json 파싱하기
				ExhibitionApiResponse2 apiResponse = mapper.readValue(json, ExhibitionApiResponse2.class);			
				List<ExhibitionApi2> itemList = apiResponse.getResponse().getBody().getItems().getItem();
				int numOfRows = apiResponse.getResponse().getBody().getNumOfRows();
				int pageNo = apiResponse.getResponse().getBody().getPageNo();
				int totalCount = apiResponse.getResponse().getBody().getTotalCount();
				
				log.debug("numOfRows: {}, pageNo: {}, totalCount: {}", numOfRows, pageNo, totalCount );
				log.debug("example retrieved items : {}", itemList);
				
				//System.out.println(itemList);
	            
	            model.addAttribute("varItemList", itemList);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    
		return "testCode/testCode";
	}


	@GetMapping("/code2")  // JSON 문자열을 자바 객체로 바꿔주는거 테스트: list of JSON-files들을  읽어 들여서 하나로 concatenate해서 한개 JSON만들고, Exhibition DTO 맵핑도 한다.
	public String codeTest2(
			Model model			
			) throws IOException {

			ClassPathResource resource = new ClassPathResource("data/json");
			String jsonSavePath = resource.getFile().getAbsolutePath();
			String originalMergeFileName = "mergeDbApiExhibitionAll.json";   // merge for all json files
			
			// 폴더 이름
	        File folder = new File(jsonSavePath);  

	        // 폴더내에 파일 목록 가져오기
	        File[] files = folder.listFiles();

	        // 파일 이름 리스트로 저장
	        List<String> fileNames = new ArrayList<>();

	        if (files != null) {
	            for (File file : files) {
	                if (file.isFile() 
	                		&& file.getName().endsWith(".json") 
	                		&& !file.getName().contains("responseObject") 
	                		&& !file.getName().contains("merge")) { // 폴더 제외, 파일만 && 특정 확장자(.json)만 필터링.(=> .startsWith(), .contains() )
	                    fileNames.add(file.getAbsolutePath()); //파일 전체 경로가 필요하다면 file.getAbsolutePath() 
	                }
	            }
	        }

	        // 출력
	        log.info("파일 목록(절대경로):");
	        for (String name : fileNames) {
	            log.info("파일이름: {}", name);
	        }
	        
	        
	        // ----------------------------------------------------
	        // ----- testing 일단 3개 json만 뭉쳐보자
	        //
	        
	        
	        // read example json file
	        JsonFileReaderService jsonFileReaderService = new JsonFileReaderService();
	        //
	        ObjectMapper mapper = new ObjectMapper();
	        //
	        List<ExhibitionApi2> resultItemsMerge = new ArrayList<>();
	        
	        //for (int i = 0; i < 3; i++) { // i < fileNames.size() // => for "mergeDbApiExhibition_test230items.json"
	        for (int i = 0; i < fileNames.size(); i++) { // i < fileNames.size() // => for "mergeDbApiExhibitionAll.json"
	        	String jsonFileAbsPath = fileNames.get(i); // json file absolute path as String
	        	log.info("머지할 파일이름: {}", jsonFileAbsPath);
	        	
	        	// 문자열로 읽기
	        	String json = jsonFileReaderService.readJsonAsString(jsonFileAbsPath);
	        	
//				// 객체로 직접 읽기
//				ExhibitionApiResponse2 response = jsonFileReaderService.readJsonAsObject("dbApiExhibition_test1.json", ExhibitionApiResponse2.class);
	        	
	        	
	        	try {
	        		
	        		// 방법1) Map 과 Object(ArrayList)로 json 파싱하기
		            Map<String, Object> map = mapper.readValue(json, Map.class);
		            // 데이터 접근
		            List itemsList = (List) map.get("itemsList");

		            resultItemsMerge.addAll(itemsList);
	        		
	        		
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	}
	        	
	        	
	        }
	        
	        // ----------------------------------------------------
			// 합쳐진 JSON 저장
    		// file saving	        
            try {
            	
	            // 0. 최종 리스트를 Map으로 감싸기
	            Map<String, List<ExhibitionApi2>> resultMap = new HashMap<>();
	            resultMap.put("itemsList", resultItemsMerge);  // {"itemsList": [ "item0": <ExhibitionApiResponse2 > , "item1": <ExhibitionApiResponse2 >, ... ]}
	            
            	// 1. 직렬화
            	// JSON 문자열로 직렬화
				String resultJsonString = mapper
				        .writerWithDefaultPrettyPrinter()
				        .writeValueAsString(resultMap);
				
				// 1. 현재 날짜+시간까지 구하기
				LocalDateTime now = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
				String formattedDateTime = now.format(formatter);
				
				// 2. 파일 이름 분리 (확장자 처리)
				int dotIndex = originalMergeFileName.lastIndexOf(".");
				String namePart = originalMergeFileName.substring(0, dotIndex);   // "dbApiExhibition"
				String extensionPart = originalMergeFileName.substring(dotIndex); // ".json"
				
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        // frontend로 전달
	        model.addAttribute("varItemList", resultItemsMerge);
			
	    
	    
		return "testCode/testCode";
	}
	
	
	@GetMapping("/code3")  // API조회로 얻은 JSON 파일을 읽어서  ExhibitionApi2(API DTO)를 Moa- Exhibition DTO로 맵핑해서 대응 필드에 값 세팅해주기
	public String codeTest3(
			Model model			
			) throws IOException  {

	
			ClassPathResource resource = new ClassPathResource("data/json");
			String jsonSavePath = resource.getFile().getAbsolutePath();
			System.out.println("jsonSavePath : " + jsonSavePath);
			
			// 폴더 이름
	        File folder = new File(jsonSavePath);  

	        // 폴더내에 파일 목록 가져오기
	        File[] files = folder.listFiles();

	        // 파일 이름 리스트로 저장
	        List<String> fileNames = new ArrayList<>();

	        String targetFileName = null; // targetFileName for API DTO -> Exhibition DTO mapping
	        if (files != null) {
	            for (File file : files) {
	            	
	            	if ( file.getName().contains("test230items") ) {
	            		targetFileName = file.getAbsolutePath(); //
	            	} else if (file.isFile() 
	                		&& file.getName().endsWith(".json") 
	                		&& !file.getName().contains("responseObject") 
	                		&& !file.getName().contains("merge")) { // 폴더 제외, 파일만 && 특정 확장자(.json)만 필터링.(=> .startsWith(), .contains() )
			                    fileNames.add(file.getAbsolutePath()); //파일 전체 경로가 필요하다면 file.getAbsolutePath() 
	                } 
	            }
	        }

	        // 출력
	        log.info("타겟 파일 이름 : {}", targetFileName);
	        
	        
	        // ----------------------------------------------------
	        // ----- 타겟 json파일을(API DTO) 읽어서 DTO 만들어보자 (이때 긴내용 요약/ Null 값 대처 고민)
	        //
	        
	        
	        // read example json file
	        JsonFileReaderService jsonFileReaderService = new JsonFileReaderService();

	        
	        // API DTO 객체
	        //List<ExhibitionApi2> resultItemsMerge = new ArrayList<>();
	        
	        // Exhibition DTO 객체
	        List<ExhibitionOldOld> exhibitionDtoItems = new ArrayList<>();
	        
        	// read targett json file
	        // 문자열로 읽기
	        String json = jsonFileReaderService.readJsonAsString(targetFileName);
	                
	        //
	        ObjectMapper mapper = new ObjectMapper();
	        
	        try {

	        	Map<String,  List<ExhibitionApi2>> map = mapper.readValue(json, 
	        			new TypeReference<Map<String, List<ExhibitionApi2>>>() {}
	        	); // ExhibitionApi2 를 위한 Mapper
	        	
	        	// 데이터 접근
	        	List<ExhibitionApi2> itemsList = map.get("itemsList"); // 오류	: class java.util.LinkedHashMap cannot be cast to class edu.og.api.dbApiExhibition.model.dto.ExhibitionApi2  
	        	//List<ExhibitionApi2> itemsList = (List<ExhibitionApi2>) map.get("itemsList");
	        	
	        	
	        	/////////////////////////////////////////////////////////////////////////////
	        	// 여기서 for문 돌려 itemsList의 한 원소값 꺼내서 exhibitionDtoItem에 해당 필에 값 assign한다
	        	// 또한, exhibitionDtoItem의 다른 필드에는 적절한 mock데이터 넣어서 mock-JSON생성한다.
	        	for (int i=0; i < itemsList.size(); i++ ) {
	        		
	        		
	        		ExhibitionOldOld exhibitionDtoItem = new ExhibitionOldOld(); // 런타임에러
	        		// Exhibition exhibitionDtoItem을 for문 밖에서 한 번만 생성하면 Java에서 객체는 참조 타입이므로, 
	        		// 같은 객체를 계속 수정해서 List에 추가하면 List의 모든 요소가 동일한 객체를 가리키게 됩니다.
	        		
	        		
	        		////////////////////////////////////////////////////////////////////////////
	        		//////////////// [ mock data assignment to Exhibition DTO  ] ////////////////////////	        		
	        		// a) exhibitNo; // "Board_No" (게시판 글번호; PK)
	        		exhibitionDtoItem.setExhibitNo(i);
	        		       		
	        		// b) exhibitUpdateDate;
	        		exhibitionDtoItem.setExhibitUpdateDate(null);
	        		
	        		// c) readCount
	        		int num1 = (int)(Math.random() * 10); // 0 이상 9 이하
	        		exhibitionDtoItem.setReadCount(num1);
	        		
	        		// d) communityCode (전시게시판: 3)
	        		exhibitionDtoItem.setCommunityCode(3);
	        		
	        		// e) likeCount
	        		int num2 = (int)(Math.random() * 10); // 0 이상 9 이하
	        		exhibitionDtoItem.setLikeCount(num2);
	        		
	        		// f) memberNickname: "한국문화정보원" (공공데이터 API) 
	        		exhibitionDtoItem.setMemberNickname("한국문화정보원");
	        		
	        		// g) memberNo: 3 (임의값 할당)
	        		exhibitionDtoItem.setMemberNo(3);
	        		
	        		// h) profileImage
	        		String kcisaProfileImage = "/images/member/penguin.jpeg"; // spring boot web맵핑(@{})경로
	        		exhibitionDtoItem.setProfileImage(kcisaProfileImage);
	        		
	        		// i) thumbnail
	        		String thumbnail = "/images/member/penguin02_400x400.jpg";  // spring boot web맵핑(@{})경로
	        		String tmp_imageObject_chk =  itemsList.get(i).getImageObject(); // 이걸 split해서 폴더경로(imagePath)와 파일이름(imageOriginal)으로 가른다
	        		if (tmp_imageObject_chk == null || tmp_imageObject_chk.trim().isEmpty()){ 
	        			thumbnail = "/images/exhibition/monet_pond.jpg"; // spring boot web맵핑(@{})경로
	        		} else { // ImageObject 가 null아닐때는, thumnail에 imageObject값(전시 포스터의 url 경로)을 담는다. 
	        			thumbnail = tmp_imageObject_chk; // 나중에도 
	        		}
	        		exhibitionDtoItem.setThumbnail(thumbnail);
	        		
	        		// j) imageList
	        		////// BOARD_IMG 테이블
	        		// 0) from imageObject	("IMAGE_OBJECT")		
	        		String tmp_imageObject =  itemsList.get(i).getImageObject(); 
	        		exhibitionDtoItem.setExhibitImgObject(tmp_imageObject);
	        		// ImageObject 가 null일때는, 디폴트 monet_pond.jpg그림 경로담기
	        		if (tmp_imageObject == null || tmp_imageObject.trim().isEmpty()){ 
	        			tmp_imageObject = "/images/board/exhibition/monet_pond.jpg";
	        		}         		
	        		
	        		// # 예시) IMAGE_OBJECT	"https://www.mmca.go.kr/upload/exhibition/2025/06/2025061711274643617296.png"
	        		String[] parts = tmp_imageObject.split("/");
	        		String imgObjName = parts[parts.length-1]; // 이미지이름: "2025061711274643617296.png"
	        		String imgObjPath = String.join("/", Arrays.copyOfRange(parts, 0, parts.length - 1)) + "/"; // 이미지경로: "https://www.mmca.go.kr/upload/exhibition/2025/06/" // frontend 에서 경로명 사용에 맨 끝 "/"이 필요함 

	        		
	        		List<BoardImageOldOld> tmp_imgList = new ArrayList<>();
	        		BoardImageOldOld tmp_img = new BoardImageOldOld();
	        		tmp_img.setBoardNo(i); // "Board_No" (게시판 글번호; PK)를 참조키; 여기서는 임의값 i할당
	        		tmp_img.setImageNo(i); // i,  임의값 할당
	        		tmp_img.setImagePath(imgObjPath); // image folder path, 
	        		tmp_img.setImageOriginal(imgObjName); // original image name
	        		tmp_img.setImageReName(imgObjName); // renamed image name, 임의할당,  일단 orginal image와 같게 (우리는 전시포스터때문에 imageOriginal밖에는 못씀)
	        		tmp_img.setImageOrder(0); // 0, thumbnail, 임의할당
	        		// collecting tmp_img 
	        		tmp_imgList.add(tmp_img);
	        		exhibitionDtoItem.setImageList(tmp_imgList);
	        		
	        		
	        		////////////////////////////////////////////////////////////////////////////
	        		//////////////// [ API DTO <-> Exhibition DTO 맴핑 ] ////////////////////////
	        		///// API 데이터 filtering flag
	        			        		
	        		////// BOARD 테이블에
	        		// 1) from title ("TITLE")						// ==> NOT-NULL
	        		String tmp_title =  itemsList.get(i).getTitle();
	        		// length filtering:
	        		if (tmp_title.length() >  150) continue; // 'TITLE',          # BOARD_TITLE: VARCHAR2(150),          NOT_NULL
	        		exhibitionDtoItem.setExhibitTitle(tmp_title); 
	        		
	        		// 2) from collectDate ("COLLECTED_DATE")		// ==> NOT-NULL
	        		String tmp_collectDate = itemsList.get(i).getCollectDate();
	        		exhibitionDtoItem.setExhibitCreateDate(tmp_collectDate);
	        		
	        		
	        		// 3) from description ("DESCRIPTION") 			// ==> NOT-NULL (imputation needed)
	        		String tmp_description = itemsList.get(i).getDescription();
	        		// NULL imputation (null, 빈문자열, 공백만 있는 문자열)
	        		if (tmp_description == null || tmp_description.trim().isEmpty()){ 
	        			tmp_description = "전시 내용에 대해서는 전시 주관 기관으로 직접 문의해주시기 바랍니다.";
	        		}
	        		if (tmp_description.length() > 4000) continue; // "DESCRIPTION",    # BOARD_CONTENT: VARCHAR2(4000)        NULL
	        		exhibitionDtoItem.setExhibitContent(tmp_description);
	        		
	        		//////////
	        		
	        		// 4) from subDescription ("SUB_DESCRIPTION")  
	        		String tmp_subDescription = itemsList.get(i).getSubDescriptioin();
	        		if (tmp_subDescription != null) {
	        			if (tmp_subDescription.length() > 200) continue; // "SUB_DESCRIPTION",# EXHIBIT_SUB_TITLE: VARCHAR2(200)     NULL
	        		}
	        		exhibitionDtoItem.setExhibitSubTitle(tmp_subDescription);
	        		
	        		
	        		// 5) from period ("PERIOD")                    
	        		String tmp_period = itemsList.get(i).getPeriod();
	        		if (tmp_period != null) {
	        			if (tmp_period.length() > 50) continue;     // "PERIOD",         # EXHIBIT_DATE:VARCHAR2(50)            NULL
	        		}
	        		exhibitionDtoItem.setExhibitDate(tmp_period);
	        		
	        		// 6) eventSite ("EVENT_SITE")
	        		String tmp_eventSite = itemsList.get(i).getEventSite();
	        		if (tmp_eventSite != null) {
	        			if (tmp_eventSite.length() > 100) continue; // "EVENT_SITE",     # EXHIBIT_LOCATION: VARCHAR2(100)      NULL    
	        		}
	        		exhibitionDtoItem.setExhibitLocation(tmp_eventSite);
	        		
	        		// 7) genre ("GENRE")	
	                // 랜덤 인덱스를 선택해서 문자열 추출
	        		String[] strings = {"현대미술", "미디어아트", "사진전", "설치미술", "전통미술", "민속미술", "장르불분명(Unclassified)", "복합장르(Hybrid Art)", "기획전시(Curated Exhibition)"};
	        		Random random = new Random();
	                String randomString = strings[random.nextInt(strings.length)];
	                
	        		String tmp_genre = itemsList.get(i).getGenre();
	        		// NULL imputation (null, 빈문자열, 공백만 있는 문자열)
	        		if (tmp_genre == null || tmp_genre.trim().isEmpty()){ 
	        			// placeholder for later work
	        			tmp_genre = randomString;
	        		} else { 
	        			if (tmp_genre.length() > 50) continue; // "GENRE",          # EXHIBIT_GENRE: VARCHAR2(50)          NULL
	        			tmp_genre = randomString; // 그냥 Null-imputation과 똑같이, 기존것이 [None, '예정전시', '전시', '과거전시', '현재전시', '특별전']로 쓸수없는 정보
	        		}
	        		exhibitionDtoItem.setExhibitGenre(tmp_genre);
	        		        		
	        		
	        		// 8) audience ("AUDIENCE")
	        		String tmp_audience = itemsList.get(i).getAudience();
	        		if (tmp_audience != null) {
	        			if (tmp_audience.length() > 50) continue; // "AUDIENCE",     # EXHIBIT_AUDIENCE: VARCHAR2(50)       NULL
	        		}
	        		exhibitionDtoItem.setExhibitAudience(tmp_audience);
	        		
	        		// 9) charge ("CHARGE") 						// ==> NOT-NULL (imputation needed)
	        		String tmp_charge = itemsList.get(i).getCharge();
	        		// NULL imputation (null, 빈문자열, 공백만 있는 문자열)
	        		if (tmp_charge == null || tmp_charge.trim().isEmpty()){ 
	        			tmp_charge = "0";
	        		} else {
	        			if (tmp_charge.length() > 50) continue; // "CHARGE",         # EXHIBIT_CHARGE: NUMBER               NOT_NULL (STRING(50) -> NUMBER CONV)
	        		}
	        		exhibitionDtoItem.setExhibitCharge(tmp_charge);
	        		
	        		// 10) author ("AUTHOR")
	        		String tmp_author = itemsList.get(i).getAuthor();
	        		if (tmp_author != null) {
	        			if (tmp_author.length() > 500) continue; // "AUTHOR",         # AUTHOR_NAME: VARCHAR2(500)           NULL
	        		}
	        		exhibitionDtoItem.setExhibitAuthor(tmp_author);
	        		
	        		// 11) institutionName ("CNTC_INSTT_NM") 		// ==> NOT-NULL (also consider "EXHIBIT_INST_TEL"); INSTITUTION 테이블("EXHIBIT_INST_NAME" and "EXHIBIT_INST_TEL")		
	        		String tmp_institutionName = itemsList.get(i).getInstitutionName();
	        		if (tmp_institutionName != null) {
	        			if (tmp_institutionName.length() > 200) continue; //  "CNTC_INSTT_NM",    # EXHIBIT_INST_NAME: VARCHAR2(200)     NOT_NULL
	        		}
	        		exhibitionDtoItem.setExhibitInstitution(tmp_institutionName);
	        		
	        		// 12) contributor ("CONTRIBUTOR") 				// => CONTRIBUTOR 테이블 "EXHIBIT_HOST" and "EXHIBIT_SUPPORT"
	        		String tmp_contributor = itemsList.get(i).getContributor();
	        		if (tmp_contributor != null) {
	        			if (tmp_contributor.length() > 200) continue; // "CONTRIBUTOR",    # EXHIBIT_HOST: VARCHAR2(200)          NULL
	        		}
	        		exhibitionDtoItem.setExhibitContributor(tmp_contributor);
	        		
	        		// 13) contactPoint ("CONTACT_POINT") 			// ==> NOT-NULL (imputation needed) 
	        		// 박물관 연락처 Map for Null-imputation
	                Map<String, String> mapTel = new HashMap<>();
	                mapTel.put( "국립경주박물관", "054-740-7548" );
	                mapTel.put( "국립공주박물관", "041-850-6300" );
	                mapTel.put( "국립광주박물관", "062-570-7800" );
	                mapTel.put( "국립김해박물관", "055-320-6800" );
	                mapTel.put( "국립대구박물관", "053-760-8580" );
	                mapTel.put( "국립박물관문화재단", "1544-5955" );
	                mapTel.put( "국립부여박물관", "041-833-8562" );
	                mapTel.put( "국립아시아문화전당", "051-704-9270" );
	                mapTel.put( "국립어린이청소년도서관", "02-3413-4800" );
	                mapTel.put( "국립익산박물관", "063-830-0900" );
	                mapTel.put( "국립제주박물관", "학예연구실 이재호 학예연구사 (064-720-8104)" );
	                mapTel.put( "국립중앙박물관", "02-2077-9540" );
	                mapTel.put( "국립진주박물관", "055-742-5951" );
	                mapTel.put( "국립청주박물관", "043-229-6500" );
	                mapTel.put( "국립춘천박물관", "033-260-1500" );
	                mapTel.put( "국립한글박물관", "02-2124-6200" );
	                mapTel.put( "국립현대미술관", "02-3701-9500" );
	                mapTel.put( "대한민국역사박물관", "02-3703-9200" );
	                mapTel.put( "예술의전당", "02-325-1077" );
	                mapTel.put( "태권도진흥재단", "063-320-0115" );
	                mapTel.put( "한국영상자료원", "02-3153-2001" );
	                mapTel.put( "한국예술종합학교", "karts.space.2025@gmail.com");
	        		
	        		String tmp_contactPoint = itemsList.get(i).getContactPoint();
	        		// NULL imputation (null, 빈문자열, 공백만 있는 문자열)
	        		if (tmp_contactPoint == null || tmp_contactPoint.trim().isEmpty()){ 
	        			tmp_contactPoint = mapTel.get(tmp_institutionName); //)
	        		} else {
	        			if (tmp_contactPoint.length() > 100) continue;  // "CONTACT_POINT",  # EXHIBIT_CONTACT: VARCHAR2(100)       NOT_NULL
	        		}
	        		exhibitionDtoItem.setExhibitContact(tmp_contactPoint);
	        		
	        		//////////////////////////////////////////////////////////
	        		// FINALY, collect the mapped DTO
	        		exhibitionDtoItems.add(exhibitionDtoItem);
	        	}
	        	
	        	
	        	
	        	//resultItemsMerge.addAll(itemsList);
	        	
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }

	        
	        
	        //log.info("collected Exhibition DTO : {}", exhibitionDtoItems.get(0)); // ok...
	        
	        // ----------------------------------------------------
			// 합쳐진 JSON 저장
    		// file saving	        
	        ObjectMapper mapper2 = new ObjectMapper();
	        
        	
            try {
            	
            	
	            // 0. 최종 리스트를 Map으로 감싸기
	            Map<String, List<ExhibitionOldOld>> resultMap = new HashMap<>();
	            resultMap.put("itemsList", exhibitionDtoItems);  // {"itemsList": [ "item0": <ExhibitionApiResponse2 > , "item1": <ExhibitionApiResponse2 >, ... ]}
	            
				
				String resultJsonString = mapper2
				        .writerWithDefaultPrettyPrinter()
				        .writeValueAsString(resultMap);				
				
				// 1. 현재 날짜+시간까지 구하기
				LocalDateTime now = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
				String formattedDateTime = now.format(formatter);
				
				// 2. 파일 이름 분리 (확장자 처리) // mergeDbApiExhibition_test230items_20250929_165449.json
				String[] partsTarget = targetFileName.split("/");
				String targetFileNameCp = partsTarget[partsTarget.length-1];
				int dotIndexCp = targetFileNameCp.lastIndexOf(".");
				String namePartCp = targetFileNameCp.substring(0, dotIndexCp);   // "mergeDbApiExhibition_test230items_20250929_165449"
				String extensionPartCp = targetFileNameCp.substring(dotIndexCp); // ".json"
				// 그다음에 앞에 prefix만 떼어내어 그걸 사용하자
				String[] parts = namePartCp.split("_");
				String prefix = parts[0]; // "mergeDbApiExhibition"
				log.info("namePart : {}", namePartCp);
				log.info("prefix : {}", prefix);
				
				// 3. 날짜를 파일 이름에 추가
				//String fileNameWithDate = namePart + "_" + formattedDate + extensionPart; // "dbApiExhibition_20250929.json"
				// 3. 날짜+시간을 파일 이름에 추가
				String fileNameWithDate = prefix + "_mockSM230_" + formattedDateTime + extensionPartCp; // for "mergeDbApiExhibition_test230items_20250929_165449.json"
				//String fileNameWithDate = prefix + "_mockLG7630_" + formattedDateTime + extensionPartCp; // for "mergeDbApiExhibitionAll_7630items_20250929_165842.json"
				log.info("saving fileNameWithDate : {}", fileNameWithDate);
				log.info("jsonSavePath : {}", jsonSavePath);
				
				// 4. 전체 경로 만들기
				Path directory = Paths.get(jsonSavePath.replace("/bin/main", "/src/main/resources")); 
				Path filePath = directory.resolve(fileNameWithDate);			
				String filePathString = filePath.toString();
				
				log.info("saving filePathString : {}", filePathString); 
				
				JsonFileWriterService jsonFileService = new JsonFileWriterService();
				jsonFileService.saveJsonToFileByBufferedWriter(resultJsonString, filePathString);
				
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        // frontend로 전달
	        model.addAttribute("varItemList", exhibitionDtoItems);
			
	    
	    
		return "testCode/testCode";
	}	
	

	
	@GetMapping("/code4")  // AP조회로 얻은 JSON 파일을 읽어서  ExhibitionApi2(API DTO)를 Moa- Exhibition DTO로 맵핑해서 대응 필드에 값 세팅해주기(/code3) => *mockSM230*.json파일 loading test
	public String codeTest4(
			Model model			
			) throws IOException {

			ClassPathResource resource = new ClassPathResource("data/json");
			String jsonSavePath = resource.getFile().getAbsolutePath();
			
			// 폴더 이름
	        File folder = new File(jsonSavePath);  

	        // 폴더내에 파일 목록 가져오기
	        File[] files = folder.listFiles();

	        // 파일 이름 리스트로 저장
	        List<String> fileNames = new ArrayList<>();

	        String targetFileName = null; // targetFileName for API DTO -> Exhibition DTO mapping
	        if (files != null) {
	            for (File file : files) {
	            	
	            	if ( file.getName().contains("mockSM230") ) { // ==> 실제는 209개 items
	            		targetFileName = file.getAbsolutePath(); //
	            	} else if (file.isFile() 
	                		&& file.getName().endsWith(".json") 
	                		&& !file.getName().contains("responseObject") 
	                		&& !file.getName().contains("merge")) { // 폴더 제외, 파일만 && 특정 확장자(.json)만 필터링.(=> .startsWith(), .contains() )
			                    fileNames.add(file.getAbsolutePath()); //파일 전체 경로가 필요하다면 file.getAbsolutePath() 
	                } 
	            }
	        }

	        // 출력

	        log.info("타겟 파일 이름 : {}", targetFileName);
	        
	        
	        // ----------------------------------------------------
	        // ----- 타겟 json파일을(API DTO) 읽어서 DTO 만들어보자 (이때 긴내용 요약/ Null 값 대처 고민)
	        //
	        
	        
	        // read example json file
	        JsonFileReaderService jsonFileReaderService = new JsonFileReaderService();

	        
	        // API DTO 객체
	        //List<ExhibitionApi2> resultItemsMerge = new ArrayList<>();
	        
	        // Exhibition DTO 객체
	        List<ExhibitionOldOld> exhibitionDtoItems = new ArrayList<>();
	        
        	// read targett json file
	        // 문자열로 읽기
	        String json = jsonFileReaderService.readJsonAsString(targetFileName);
	                
	        //
	        ObjectMapper mapper = new ObjectMapper();
	        
	        try {

	        	Map<String,  List<ExhibitionOldOld>> map = mapper.readValue(json, 
	        			new TypeReference<Map<String, List<ExhibitionOldOld>>>() {}
	        	); // ExhibitionApi2 를 위한 Mapper
	        	
	        	// 데이터 접근
	        	List<ExhibitionOldOld> itemsList = map.get("itemsList"); // 오류	: class java.util.LinkedHashMap cannot be cast to class edu.og.api.dbApiExhibition.model.dto.ExhibitionApi2  
	        	
	        	// frontend로 전달
	        	model.addAttribute("varItemList", itemsList);
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    
		return "testCode/testCode";
	}		
	
	
	
	@GetMapping("/code5")  // AP조회로 얻은 JSON 파일을 읽어서  ExhibitionApi2(API DTO)를 Moa- Exhibition DTO로 맵핑해서 대응 필드에 값 세팅해주기(/code3) ==>"mockLG7630" loading test
	public String codeTest5(
			Model model			
			) throws IOException {

			ClassPathResource resource = new ClassPathResource("data/json");
			String jsonSavePath = resource.getFile().getAbsolutePath();
			
			// 폴더 이름
	        File folder = new File(jsonSavePath);  

	        // 폴더내에 파일 목록 가져오기
	        File[] files = folder.listFiles();

	        // 파일 이름 리스트로 저장
	        List<String> fileNames = new ArrayList<>();

	        String targetFileName = null; // targetFileName for API DTO -> Exhibition DTO mapping
	        if (files != null) {
	            for (File file : files) {
	            	
	            	if ( file.getName().contains("mockLG7630") ) { // ==> 실제는 6838개 items
	            		targetFileName = file.getAbsolutePath(); //
	            	} else if (file.isFile() 
	                		&& file.getName().endsWith(".json") 
	                		&& !file.getName().contains("responseObject") 
	                		&& !file.getName().contains("merge")) { // 폴더 제외, 파일만 && 특정 확장자(.json)만 필터링.(=> .startsWith(), .contains() )
			                    //fileNames.add(file.getName()); // 파일 이름만.
			                    fileNames.add(file.getAbsolutePath()); //파일 전체 경로가 필요하다면 file.getAbsolutePath() 
	                } 
	            }
	        }

	        // 출력
//	        log.info("파일 목록(절대경로):");
//	        for (String name : fileNames) {
//	            log.info("파일이름: {}", name);
//	        }
	        log.info("타겟 파일 이름 : {}", targetFileName);
	        
	        
	        // ----------------------------------------------------
	        // ----- 타겟 json파일을(API DTO) 읽어서 DTO 만들어보자 (이때 긴내용 요약/ Null 값 대처 고민)
	        //
	        
	        
	        // read example json file
	        JsonFileReaderService jsonFileReaderService = new JsonFileReaderService();

	        
	        // API DTO 객체
	        //List<ExhibitionApi2> resultItemsMerge = new ArrayList<>();
	        
	        // Exhibition DTO 객체
	        List<ExhibitionOldOld> exhibitionDtoItems = new ArrayList<>();
	        
        	// read targett json file
	        // 문자열로 읽기
	        String json = jsonFileReaderService.readJsonAsString(targetFileName);
	                
	        //
	        ObjectMapper mapper = new ObjectMapper();
	        
	        try {
	        	//ObjectMapper mapper = new ObjectMapper();
	        	
	        	// Map 과 Object(ArrayList)로 json 파싱하기
	        	//Map<String, Object> map = mapper.readValue(json, Map.class);
	        	//Map<String, List<ExhibitionApi2>> map = mapper.readValue(json, Map.class);
	        	Map<String,  List<ExhibitionOldOld>> map = mapper.readValue(json, 
	        			new TypeReference<Map<String, List<ExhibitionOldOld>>>() {}
	        	); // ExhibitionApi2 를 위한 Mapper
	        	
	        	// 데이터 접근
	        	List<ExhibitionOldOld> itemsList = map.get("itemsList"); // 오류	: class java.util.LinkedHashMap cannot be cast to class edu.og.api.dbApiExhibition.model.dto.ExhibitionApi2  
	        	//List<ExhibitionApi2> itemsList = (List<ExhibitionApi2>) map.get("itemsList");
	        	
	        	// frontend로 전달
	        	model.addAttribute("varItemList", itemsList);
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    	        
	        
		return "testCode/testCode";
	}		
	
	

	
	@GetMapping("/code6")  	// AP조회로 얻은 JSON 파일을 읽어서  ExhibitionApi2(API DTO)를 Moa- Exhibition DTO로 맵핑해서 대응 필드에 값 세팅해주기(/code3) 
							// => *mockSM230*.json 파일 loading test(절대경로) => Controller 폴더에 *mockSM230*.json 파일 넣고, loading test(current directory 상대경로) 
	public String codeTest6 (
			Model model			
			) throws IOException {

	
	
	        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; // targetFileName for API DTO -> Exhibition DTO mapping
	        
	        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
	        
	        if (inputStream == null) {
	            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
	        }
	        
	        // 출력

	        log.info("타겟 파일 이름 : {}", targetFileName);
	        
	        
	        // ----------------------------------------------------
	        // ----- 타겟 json파일을(API DTO) 읽어서 DTO 만들어보자 (이때 긴내용 요약/ Null 값 대처 고민)
	        //
	        
	       

	        
	        // API DTO 객체
	        //List<ExhibitionApi2> resultItemsMerge = new ArrayList<>();
	        
	        // Exhibition DTO 객체
	        List<ExhibitionOldOld> exhibitionDtoItems = new ArrayList<>();
	        
	                
	        //
	        ObjectMapper mapper = new ObjectMapper();
	        
	        try {

	        	Map<String,  List<ExhibitionOldOld>> map = mapper.readValue(inputStream, 
	        			new TypeReference<Map<String, List<ExhibitionOldOld>>>() {}
	        			); // ExhibitionApi2 를 위한 Mapper
	        	
	        	// 데이터 접근
	        	List<ExhibitionOldOld> itemsList = map.get("itemsList"); // 오류	: class java.util.LinkedHashMap cannot be cast to class edu.og.api.dbApiExhibition.model.dto.ExhibitionApi2  
	        	//List<ExhibitionApi2> itemsList = (List<ExhibitionApi2>) map.get("itemsList");
	        	
	        	// frontend로 전달
	        	model.addAttribute("varItemList", itemsList);
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    
		return "testCode/testCode";
	}		
	
	
	
	
	@GetMapping("/code7")  	// API조회로 얻은 JSON 파일을 읽어서  ExhibitionApi2(API DTO)를 Moa- Exhibition DTO로 맵핑해서 대응 필드에 값 세팅해주기 
							// => revision: /images/member -> /images/board/exhitbition/member; /images/exhibition -> /images/board/exhibition
	public String codeTest7(
			Model model			
			) throws IOException {

			ClassPathResource resource = new ClassPathResource("data/json");
			String jsonSavePath = resource.getFile().getAbsolutePath();
			
			// 폴더 이름
	        File folder = new File(jsonSavePath);  

	        // 폴더내에 파일 목록 가져오기
	        File[] files = folder.listFiles();

	        // 파일 이름 리스트로 저장
	        List<String> fileNames = new ArrayList<>();

	        String targetFileName = null; // targetFileName for API DTO -> Exhibition DTO mapping
	        if (files != null) {
	            for (File file : files) {
	            	
	            	if ( file.getName().contains("test230items") ) {
	            		targetFileName = file.getAbsolutePath(); // for mockSM230
	            	} else if (file.isFile() 
	                		&& file.getName().endsWith(".json") 
	                		&& !file.getName().contains("responseObject") 
	                		&& !file.getName().contains("merge")) { // 폴더 제외, 파일만 && 특정 확장자(.json)만 필터링.(=> .startsWith(), .contains() )
			                    //fileNames.add(file.getName()); // 파일 이름만.
			                    fileNames.add(file.getAbsolutePath()); //파일 전체 경로가 필요하다면 file.getAbsolutePath() 
	                } 
	            }
	        }

	        // 출력
	        log.info("타겟 파일 이름 : {}", targetFileName);
	        
	        
	        // ----------------------------------------------------
	        // ----- 타겟 json파일을(API DTO) 읽어서 DTO 만들어보자 (이때 긴내용 요약/ Null 값 대처 고민)
	        //
	        
	        
	        // read example json file
	        JsonFileReaderService jsonFileReaderService = new JsonFileReaderService();

	        
	        // API DTO 객체
	        //List<ExhibitionApi2> resultItemsMerge = new ArrayList<>();
	        
	        // Exhibition DTO 객체
	        List<ExhibitionOldOld> exhibitionDtoItems = new ArrayList<>();
	        
        	// read targett json file
	        // 문자열로 읽기
	        String json = jsonFileReaderService.readJsonAsString(targetFileName);
	                
	        //
	        ObjectMapper mapper = new ObjectMapper();
	        
	        try {
	        	//ObjectMapper mapper = new ObjectMapper();
	        	
	        	// Map 과 Object(ArrayList)로 json 파싱하기
	        	//Map<String, Object> map = mapper.readValue(json, Map.class);
	        	//Map<String, List<ExhibitionApi2>> map = mapper.readValue(json, Map.class);
	        	Map<String,  List<ExhibitionApi2>> map = mapper.readValue(json, 
	        			new TypeReference<Map<String, List<ExhibitionApi2>>>() {}
	        	); // ExhibitionApi2 를 위한 Mapper
	        	
	        	// 데이터 접근
	        	List<ExhibitionApi2> itemsList = map.get("itemsList"); // 오류	: class java.util.LinkedHashMap cannot be cast to class edu.og.api.dbApiExhibition.model.dto.ExhibitionApi2  
	        	//List<ExhibitionApi2> itemsList = (List<ExhibitionApi2>) map.get("itemsList");
	        	
	        	/////////////////////////////////////////////////////////////////////////////
	        	// 여기서 for문 돌려 itemsList의 한 원소값 꺼내서 exhibitionDtoItem에 해당 필에 값 assign한다
	        	// 또한, exhibitionDtoItem의 다른 필드에는 적절한 mock데이터 넣어서 mock-JSON생성한다.
	        	for (int i=0; i < itemsList.size(); i++ ) {
	        		
	        		
	        		ExhibitionOldOld exhibitionDtoItem = new ExhibitionOldOld(); // 런타임에러
	        		// Exhibition exhibitionDtoItem을 for문 밖에서 한 번만 생성하면 Java에서 객체는 참조 타입이므로, 
	        		// 같은 객체를 계속 수정해서 List에 추가하면 List의 모든 요소가 동일한 객체를 가리키게 됩.
	        		
	        		
	        		////////////////////////////////////////////////////////////////////////////
	        		//////////////// [ mock data assignment to Exhibition DTO  ] ////////////////////////	        		
	        		// a) exhibitNo; // "Board_No" (게시판 글번호; PK)
	        		exhibitionDtoItem.setExhibitNo(i);
	        		       		
	        		// b) exhibitUpdateDate;
	        		exhibitionDtoItem.setExhibitUpdateDate(null);
	        		
	        		// c) readCount
	        		int num1 = (int)(Math.random() * 10); // 0 이상 9 이하
	        		exhibitionDtoItem.setReadCount(num1);
	        		
	        		// d) communityCode (전시게시판: 3)
	        		exhibitionDtoItem.setCommunityCode(3);
	        		
	        		// e) likeCount
	        		int num2 = (int)(Math.random() * 10); // 0 이상 9 이하
	        		exhibitionDtoItem.setLikeCount(num2);
	        		
	        		// f) memberNickname: "한국문화정보원" (공공데이터 API) 
	        		exhibitionDtoItem.setMemberNickname("한국문화정보원");
	        		
	        		// g) memberNo: 3 (임의값 할당)
	        		exhibitionDtoItem.setMemberNo(10); // 10:한국문화정보원, 11:나챗봇
	        		
	        		// h) profileImage
	        		String kcisaProfileImage = "/images/board/exhibition/member/penguin.jpeg"; // spring boot web맵핑(@{})경로
	        		exhibitionDtoItem.setProfileImage(kcisaProfileImage);
	        		
	        		// i) thumbnail
	        		String thumbnail = "/images/board/exhibition/member/penguin02_400x400.jpg";  // spring boot web맵핑(@{})경로
	        		String tmp_imageObject_chk =  itemsList.get(i).getImageObject(); // 이걸 split해서 폴더경로(imagePath)와 파일이름(imageOriginal)으로 가른다
	        		// ImageObject 가 null일때는, thumbnail에 디폴트 monet_pond.jpg그림 경로(로컬 이미지경로)담기
	        		if (tmp_imageObject_chk == null || tmp_imageObject_chk.trim().isEmpty()){ 
	        			thumbnail = "/images/board/exhibition/monet_pond.jpg"; // spring boot web맵핑(@{})경로
	        		} else { // ImageObject 가 null아닐때는, thumnail에 imageObject값(전시 포스터의 url 경로)을 담는다. 
	        			thumbnail = tmp_imageObject_chk; // 나중에도 
	        		}
	        		exhibitionDtoItem.setThumbnail(thumbnail);
	        		
	        		// j) imageList
	        		////// BOARD_IMG 테이블
	        		// 0) from imageObject	("IMAGE_OBJECT")		
	        		String tmp_imageObject =  itemsList.get(i).getImageObject(); // 이걸 split해서 폴더경로(imagePath)와 파일이름(imageOriginal)으로 가른다
	        		exhibitionDtoItem.setExhibitImgObject(tmp_imageObject);
	        		// ImageObject 가 null일때는, 디폴트 monet_pond.jpg그림 경로담기
	        		if (tmp_imageObject == null || tmp_imageObject.trim().isEmpty()){ 
	        			tmp_imageObject = "/images/board/exhibition/monet_pond.jpg";
	        		} // 이제, tmp_imageObject에는 포스터 url경로(있을경우) 혹은 default값(url값 없을경우)이 담겨 null이 아니다.	        		
	        		
	        		// # 예시) IMAGE_OBJECT	"https://www.mmca.go.kr/upload/exhibition/2025/06/2025061711274643617296.png"
	        		//String imgObjName = tmp_imageObject.split("/")[tmp_imageObject.split("/").length - 1];
	        		String[] parts = tmp_imageObject.split("/");
	        		String imgObjName = parts[parts.length-1]; // 이미지이름: "2025061711274643617296.png"
	        		//String imgObjPath = String.join("/", Arrays.copyOfRange(parts, 0, parts.length - 1)); // 이미지경로: "https://www.mmca.go.kr/upload/exhibition/2025/06" 
	        		String imgObjPath = String.join("/", Arrays.copyOfRange(parts, 0, parts.length - 1)) + "/"; // 이미지경로: "https://www.mmca.go.kr/upload/exhibition/2025/06/" // frontend 에서 경로명 사용에 맨 끝 "/"이 필요함 

	        		//log.info("imgObjPath : {}", imgObjPath);
	        		
	        		List<BoardImageOldOld> tmp_imgList = new ArrayList<>();
	        		BoardImageOldOld tmp_img = new BoardImageOldOld();
	        		tmp_img.setBoardNo(i); // "Board_No" (게시판 글번호; PK)를 참조키; 여기서는 임의값 i할당
	        		tmp_img.setImageNo(i); // i,  임의값 할당
	        		tmp_img.setImagePath(imgObjPath); // image folder path, 
	        		tmp_img.setImageOriginal(imgObjName); // original image name
	        		tmp_img.setImageReName(imgObjName); // renamed image name, 임의할당,  일단 orginal image와 같게 (우리는 전시포스터때문에 imageOriginal밖에는 못씀)	        		
	        		tmp_img.setImageOrder(0); // 0, thumbnail, 임의할당
	        		// collecting tmp_img 
	        		tmp_imgList.add(tmp_img);
	        		exhibitionDtoItem.setImageList(tmp_imgList);
	        		
	        		
	        		////////////////////////////////////////////////////////////////////////////
	        		//////////////// [ API DTO <-> Exhibition DTO 맴핑 ] ////////////////////////
	        		///// API 데이터 filtering flag
	        			        		
	        		////// BOARD 테이블에
	        		// 1) from title ("TITLE")						// ==> NOT-NULL
	        		String tmp_title =  itemsList.get(i).getTitle();
	        		// length filtering:
	        		if (tmp_title.length() >  150) continue; // 'TITLE',          # BOARD_TITLE: VARCHAR2(150),          NOT_NULL
	        		exhibitionDtoItem.setExhibitTitle(tmp_title); 
	        		
	        		// 2) from collectDate ("COLLECTED_DATE")		// ==> NOT-NULL
	        		String tmp_collectDate = itemsList.get(i).getCollectDate();
	        		exhibitionDtoItem.setExhibitCreateDate(tmp_collectDate);
	        		
	        		
	        		// 3) from description ("DESCRIPTION") 			// ==> NOT-NULL (imputation needed)
	        		String tmp_description = itemsList.get(i).getDescription();
	        		// NULL imputation (null, 빈문자열, 공백만 있는 문자열)
	        		if (tmp_description == null || tmp_description.trim().isEmpty()){ 
	        			tmp_description = "전시 내용에 대해서는 전시 주관 기관으로 직접 문의해주시기 바랍니다.";
	        		}
	        		if (tmp_description.length() > 4000) continue; // "DESCRIPTION",    # BOARD_CONTENT: VARCHAR2(4000)        NULL
	        		exhibitionDtoItem.setExhibitContent(tmp_description);
	        		
	        		//////////
	        		
	        		// 4) from subDescription ("SUB_DESCRIPTION")  
	        		String tmp_subDescription = itemsList.get(i).getSubDescriptioin();
	        		if (tmp_subDescription != null) {
	        			if (tmp_subDescription.length() > 200) continue; // "SUB_DESCRIPTION",# EXHIBIT_SUB_TITLE: VARCHAR2(200)     NULL
	        		}
	        		exhibitionDtoItem.setExhibitSubTitle(tmp_subDescription);
	        		
	        		
	        		// 5) from period ("PERIOD")                    
	        		String tmp_period = itemsList.get(i).getPeriod();
	        		if (tmp_period != null) {
	        			if (tmp_period.length() > 50) continue;     // "PERIOD",         # EXHIBIT_DATE:VARCHAR2(50)            NULL
	        		}
	        		exhibitionDtoItem.setExhibitDate(tmp_period);
	        		
	        		// 6) eventSite ("EVENT_SITE")
	        		String tmp_eventSite = itemsList.get(i).getEventSite();
	        		if (tmp_eventSite != null) {
	        			if (tmp_eventSite.length() > 100) continue; // "EVENT_SITE",     # EXHIBIT_LOCATION: VARCHAR2(100)      NULL    
	        		}
	        		exhibitionDtoItem.setExhibitLocation(tmp_eventSite);
	        		
	        		// 7) genre ("GENRE")	
	                // 랜덤 인덱스를 선택해서 문자열 추출
	        		String[] strings = {"현대미술", "미디어아트", "사진전", "설치미술", "전통미술", "민속미술", "장르불분명(Unclassified)", "복합장르(Hybrid Art)", "기획전시(Curated Exhibition)"};
	        		Random random = new Random();
	                String randomString = strings[random.nextInt(strings.length)];
	                
	        		String tmp_genre = itemsList.get(i).getGenre();
	        		// NULL imputation (null, 빈문자열, 공백만 있는 문자열)
	        		if (tmp_genre == null || tmp_genre.trim().isEmpty()){ 
	        			// placeholder for later work
	        			tmp_genre = randomString;
	        		} else { 
	        			if (tmp_genre.length() > 50) continue; // "GENRE",          # EXHIBIT_GENRE: VARCHAR2(50)          NULL
	        			tmp_genre = randomString; // 그냥 Null-imputation과 똑같이, 기존것이 [None, '예정전시', '전시', '과거전시', '현재전시', '특별전']로 쓸수없는 정보
	        		}
	        		exhibitionDtoItem.setExhibitGenre(tmp_genre);
	        		        		
	        		
	        		// 8) audience ("AUDIENCE")
	        		String tmp_audience = itemsList.get(i).getAudience();
	        		if (tmp_audience != null) {
	        			if (tmp_audience.length() > 50) continue; // "AUDIENCE",     # EXHIBIT_AUDIENCE: VARCHAR2(50)       NULL
	        		}
	        		exhibitionDtoItem.setExhibitAudience(tmp_audience);
	        		
	        		// 9) charge ("CHARGE") 						// ==> NOT-NULL (imputation needed)
	        		String tmp_charge = itemsList.get(i).getCharge();
	        		// NULL imputation (null, 빈문자열, 공백만 있는 문자열)
	        		if (tmp_charge == null || tmp_charge.trim().isEmpty()){ 
	        			tmp_charge = "0";
	        		} else {
	        			if (tmp_charge.length() > 50) continue; // "CHARGE",         # EXHIBIT_CHARGE: NUMBER               NOT_NULL (STRING(50) -> NUMBER CONV)
	        		}
	        		exhibitionDtoItem.setExhibitCharge(tmp_charge);
	        		
	        		// 10) author ("AUTHOR")
	        		String tmp_author = itemsList.get(i).getAuthor();
	        		if (tmp_author != null) {
	        			if (tmp_author.length() > 500) continue; // "AUTHOR",         # AUTHOR_NAME: VARCHAR2(500)           NULL
	        		}
	        		exhibitionDtoItem.setExhibitAuthor(tmp_author);
	        		
	        		// 11) institutionName ("CNTC_INSTT_NM") 		// ==> NOT-NULL (also consider "EXHIBIT_INST_TEL"); INSTITUTION 테이블("EXHIBIT_INST_NAME" and "EXHIBIT_INST_TEL")		
	        		String tmp_institutionName = itemsList.get(i).getInstitutionName();
	        		if (tmp_institutionName != null) {
	        			if (tmp_institutionName.length() > 200) continue; //  "CNTC_INSTT_NM",    # EXHIBIT_INST_NAME: VARCHAR2(200)     NOT_NULL
	        		}
	        		exhibitionDtoItem.setExhibitInstitution(tmp_institutionName);
	        		
	        		// 12) contributor ("CONTRIBUTOR") 				// => CONTRIBUTOR 테이블 "EXHIBIT_HOST" and "EXHIBIT_SUPPORT"
	        		String tmp_contributor = itemsList.get(i).getContributor();
	        		if (tmp_contributor != null) {
	        			if (tmp_contributor.length() > 200) continue; // "CONTRIBUTOR",    # EXHIBIT_HOST: VARCHAR2(200)          NULL
	        		}
	        		exhibitionDtoItem.setExhibitContributor(tmp_contributor);
	        		
	        		// 13) contactPoint ("CONTACT_POINT") 			// ==> NOT-NULL (imputation needed) 
	        		// 박물관 연락처 Map for Null-imputation
	                Map<String, String> mapTel = new HashMap<>();
	                mapTel.put( "국립경주박물관", "054-740-7548" );
	                mapTel.put( "국립공주박물관", "041-850-6300" );
	                mapTel.put( "국립광주박물관", "062-570-7800" );
	                mapTel.put( "국립김해박물관", "055-320-6800" );
	                mapTel.put( "국립대구박물관", "053-760-8580" );
	                mapTel.put( "국립박물관문화재단", "1544-5955" );
	                mapTel.put( "국립부여박물관", "041-833-8562" );
	                mapTel.put( "국립아시아문화전당", "051-704-9270" );
	                mapTel.put( "국립어린이청소년도서관", "02-3413-4800" );
	                mapTel.put( "국립익산박물관", "063-830-0900" );
	                mapTel.put( "국립제주박물관", "학예연구실 이재호 학예연구사 (064-720-8104)" );
	                mapTel.put( "국립중앙박물관", "02-2077-9540" );
	                mapTel.put( "국립진주박물관", "055-742-5951" );
	                mapTel.put( "국립청주박물관", "043-229-6500" );
	                mapTel.put( "국립춘천박물관", "033-260-1500" );
	                mapTel.put( "국립한글박물관", "02-2124-6200" );
	                mapTel.put( "국립현대미술관", "02-3701-9500" );
	                mapTel.put( "대한민국역사박물관", "02-3703-9200" );
	                mapTel.put( "예술의전당", "02-325-1077" );
	                mapTel.put( "태권도진흥재단", "063-320-0115" );
	                mapTel.put( "한국영상자료원", "02-3153-2001" );
	                mapTel.put( "한국예술종합학교", "karts.space.2025@gmail.com");
	        		
	        		String tmp_contactPoint = itemsList.get(i).getContactPoint();
	        		// NULL imputation (null, 빈문자열, 공백만 있는 문자열)
	        		if (tmp_contactPoint == null || tmp_contactPoint.trim().isEmpty()){ 
	        			tmp_contactPoint = mapTel.get(tmp_institutionName); //)
	        		} else {
	        			if (tmp_contactPoint.length() > 100) continue;  // "CONTACT_POINT",  # EXHIBIT_CONTACT: VARCHAR2(100)       NOT_NULL
	        		}
	        		exhibitionDtoItem.setExhibitContact(tmp_contactPoint);
	        		
	        		//////////////////////////////////////////////////////////
	        		// FINALY, collect the mapped DTO
	        		exhibitionDtoItems.add(exhibitionDtoItem);
	        	}
	        	
	        	
	        	
	        	//resultItemsMerge.addAll(itemsList);
	        	
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }

	        
	        
	        //log.info("collected Exhibition DTO : {}", exhibitionDtoItems.get(0)); // ok...
	        
	        // ----------------------------------------------------
			// 합쳐진 JSON 저장
    		// file saving	        
	        ObjectMapper mapper2 = new ObjectMapper();
	        
        	
            try {
            	
            	
	            // 0. 최종 리스트를 Map으로 감싸기
	            Map<String, List<ExhibitionOldOld>> resultMap = new HashMap<>();
	            resultMap.put("itemsList", exhibitionDtoItems);  // {"itemsList": [ "item0": <ExhibitionApiResponse2 > , "item1": <ExhibitionApiResponse2 >, ... ]}
	            
	            
            	// 1. 직렬화
            	// JSON 문자열로 직렬화
				//String resultJsonString = mapper
				//        .writerWithDefaultPrettyPrinter()
				//        .writeValueAsString(resultMap);
				
				String resultJsonString = mapper2
				        .writerWithDefaultPrettyPrinter()
				        .writeValueAsString(resultMap);				
				
				// 1. 현재 날짜+시간까지 구하기
				LocalDateTime now = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
				String formattedDateTime = now.format(formatter);
				
				// 2. 파일 이름 분리 (확장자 처리) // mergeDbApiExhibition_test230items_20250929_165449.json
				String[] partsTarget = targetFileName.split("/");
				String targetFileNameCp = partsTarget[partsTarget.length-1];
				int dotIndexCp = targetFileNameCp.lastIndexOf(".");
				String namePartCp = targetFileNameCp.substring(0, dotIndexCp);   // "mergeDbApiExhibition_test230items_20250929_165449"
				String extensionPartCp = targetFileNameCp.substring(dotIndexCp); // ".json"
				// 그다음에 앞에 prefix만 떼어내어 그걸 사용하자
				String[] parts = namePartCp.split("_");
				String prefix = parts[0]; // "mergeDbApiExhibition"
				log.info("namePart : {}", namePartCp);
				log.info("prefix : {}", prefix);
				
				// 3. 날짜를 파일 이름에 추가
				//String fileNameWithDate = namePart + "_" + formattedDate + extensionPart; // "dbApiExhibition_20250929.json"
				// 3. 날짜+시간을 파일 이름에 추가
				String fileNameWithDate = prefix + "_mockSM230rev_" + formattedDateTime + extensionPartCp; // for "mergeDbApiExhibition_test230items_20250929_165449.json"
				log.info("saving fileNameWithDate : {}", fileNameWithDate);
				log.info("jsonSavePath : {}", jsonSavePath);
				
				// 4. 전체 경로 만들기
				Path directory = Paths.get(jsonSavePath.replace("/bin/main", "/src/main/resources")); 
				Path filePath = directory.resolve(fileNameWithDate);			
				String filePathString = filePath.toString();
				
				log.info("saving filePathString : {}", filePathString); 
				
				JsonFileWriterService jsonFileService = new JsonFileWriterService();
				jsonFileService.saveJsonToFileByBufferedWriter(resultJsonString, filePathString);
				
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
	        // frontend로 전달
	        model.addAttribute("varItemList", exhibitionDtoItems);
			
	    
	    
		return "testCode/testCode";
	}	
	
	
	
}
