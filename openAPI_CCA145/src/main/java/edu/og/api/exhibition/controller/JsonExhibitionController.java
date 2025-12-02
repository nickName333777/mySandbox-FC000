package edu.og.api.exhibition.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.og.api.exhibition.model.dto.BoardImageOldOld;
import edu.og.api.exhibition.model.dto.ExhibitionOldOld;
import edu.og.api.exhibition.model.dto.PaginationOldOld;
import edu.og.api.member.model.dto.Member;
import edu.og.api.testCode.controller.JsonFileReaderService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@SessionAttributes("loginMember") 
public class JsonExhibitionController {

	@GetMapping("/exhibition/jsonExhibitionList")  // AP조회로 얻은 JSON 파일을 읽어서  ExhibitionApi2(API DTO)를 Moa- Exhibition DTO로 맵핑해서 대응 필드에 값 세팅해주기
	public String jsonExhibitionList(
			//@RequestParam(name="queryKey", required=false, defaultValue="예술의 전당") String queryKey,
			Model model			
			) {

			//ClassPathResource resource = new ClassPathResource("data/json");
			//String jsonSavePath = resource.getFile().getAbsolutePath();	
			String jsonSavePath = ".";		
			
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
	        	
	        	// Map 과 Object(ArrayList)로 json 파싱하기
	        	Map<String,  List<ExhibitionOldOld>> map = mapper.readValue(json, 
	        			new TypeReference<Map<String, List<ExhibitionOldOld>>>() {}
	        	); // ExhibitionApi2 를 위한 Mapper
	        	
	        	// 데이터 접근
	        	List<ExhibitionOldOld> itemsList = map.get("itemsList"); // 오류	: class java.util.LinkedHashMap cannot be cast to class edu.og.api.dbApiExhibition.model.dto.ExhibitionApi2  
	        	Map<String, Object> mapExhibitionServiceImpl = new HashMap<>();
	        	
	        	int listCount = 209; 	// fixed 1 for mockSM230 json 데이터
	        	int cp = 1; 			// fixed 1 for mockSM230 json 데이터
	        	PaginationOldOld pagination = new PaginationOldOld(cp, listCount);
	        	int paginationLimit = pagination.getLimit();
	        	
	        	// 1) frontend로 전달: ExhibitionServiceImpl.java에서 할일
	        	List<ExhibitionOldOld> itemsListPageLimit10 = new ArrayList<>(itemsList.subList(0, paginationLimit));
	        	
				mapExhibitionServiceImpl.put("exhibitionList", itemsListPageLimit10);
				
	        	mapExhibitionServiceImpl.put("pagination", pagination);
	        	
	        	// 2) frontend로 전달: ExhibitionController.java에서 할일
				// 조회 결과를 request scope에 세팅 후 forward
				model.addAttribute("map", mapExhibitionServiceImpl); //model : spring에서 사용하는 데이터 전달 객체 => js에서 이걸 받아 사용 (@PathVariable에 담긴 boardCode와 cp도 담겨져 넘어감)

				// 3) frontend로 전달:
				// 로그인 서비스 mock:
				// session에 로그인한 회원 정보 추가
				// Servlet -> HttpSession.setAttribute(key, value)
				// Spring -> Model + @SessionAttributes				
				Member loginMember = new Member();
				loginMember.setMemberNickname("한국문화정보원");
				loginMember.setProfileImage("/images/member/penguin.jpeg"); 				
				
				loginMember.setMemberNo(3); // 임의할당 (cf:  전시 exhibitionCode === boarcCode ===  communityCode = 3)
				
				model.addAttribute("loginMember", loginMember); 
	        	
				model.addAttribute("exhibitionCode", 3);  //boardCode === exhibitionCode === communityCode
				model.addAttribute("exhibitionName", "전시게시판");  //boardName === exhibitionName === communityName
				
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    
		return "exhibition/exhibitionList";
	}
	


	@GetMapping("/exhibition/jsonExhibitionDetail")  
	public String jsonExhibitionDetail(
			Model model			
			) {

			//ClassPathResource resource = new ClassPathResource("data/json");
			//String jsonSavePath = resource.getFile().getAbsolutePath();
			String jsonSavePath = ".";		

			
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
	                		&& !file.getName().contains("merge")) { 
			                    fileNames.add(file.getAbsolutePath()); 
	                } 
	            }
	        }

	        log.info("타겟 파일 이름 : {}", targetFileName);
	        
	        
	        // ----------------------------------------------------
	        // ----- 타겟 json파일을(API DTO) 읽어서 DTO 만들어보자 (이때 긴내용 요약/ Null 값 대처 고민)
	        
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


				ExhibitionOldOld exhibition = null;
				//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
				int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
				if (index < 0 || index >= itemsList.size()) {
					throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
				} else {
						exhibition = itemsList.get(index);
				}


	        	// 2) frontend로 전달: ExhibitionController.java에서 할일

				// 조회 결과를 request scope에 세팅 후 forward
				model.addAttribute("exhibition", exhibition); 

				// 3) frontend로 전달:
				// 로그인 서비스 mock:		
				Member loginMember = new Member();
				loginMember.setMemberNickname("한국문화정보원");
				loginMember.setProfileImage("/images/member/penguin.jpeg"); 
				
				loginMember.setMemberNo(3); // 임의할당 (cf:  전시 exhibitionCode === boarcCode ===  communityCode = 3)
				
				model.addAttribute("loginMember", loginMember); 
	        	
				model.addAttribute("exhibitionCode", 3);  //boardCode === exhibitionCode === communityCode
				model.addAttribute("exhibitionName", "전시게시판");  //boardName === exhibitionName === communityName
				
	 			// 게시글 이미지가 있는 경우
	 			BoardImageOldOld thumbnail = null; 
	 			if(!exhibition.getImageList().isEmpty()) {
					
	 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
	 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
	 					thumbnail = exhibition.getImageList().get(0); // exhibitionImage 객체
	 				}
					
	 				model.addAttribute("thumbnail", thumbnail); // 썸네일이 없는 경우 thumbnail=null -> exhibitionDetail.html에서 이제 thumbnail쓸수 있다. 2025/09/16
							
	 			}
				

	
				model.addAttribute("start", thumbnail != null ? 1 : 0); // 삼항 연산자로 더 간단히
				
				
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    
		return "exhibition/exhibitionDetail";
		//return "main";
	}

	
	
	@GetMapping("/exhibition/jsonExhibitionUpdate")  
	public String jsonExhibitionUpdate(
			Model model			
			) {

			//ClassPathResource resource = new ClassPathResource("data/json");
			//String jsonSavePath = resource.getFile().getAbsolutePath();
			String jsonSavePath = ".";		
			
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


				ExhibitionOldOld exhibition = null;
				//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
				int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
				if (index < 0 || index >= itemsList.size()) {
					throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
				} else {
						exhibition = itemsList.get(index);
				}
				

	        	// 2) frontend로 전달: ExhibitionController.java에서 할일
				// 조회 결과를 request scope에 세팅 후 forward
				model.addAttribute("exhibition", exhibition); 

				// 3) frontend로 전달:
				// 로그인 서비스 mock:	
				Member loginMember = new Member();
				loginMember.setMemberNickname("한국문화정보원");
				loginMember.setProfileImage("/images/member/penguin.jpeg"); 
				
				loginMember.setMemberNo(3); // 임의할당 (cf:  전시 exhibitionCode === boarcCode ===  communityCode = 3)
				
				model.addAttribute("loginMember", loginMember); 
	        	
				model.addAttribute("exhibitionCode", 3);  
				model.addAttribute("exhibitionName", "전시게시판");  

	 			// 게시글 이미지가 있는 경우
	 			BoardImageOldOld thumbnail = null; 
	 			if(!exhibition.getImageList().isEmpty()) {
					
	 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
	 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
	 					thumbnail = exhibition.getImageList().get(0); // exhibitionImage 객체
	 				}
					
	 				model.addAttribute("thumbnail", thumbnail); 
							
	 			}
				


				model.addAttribute("start", thumbnail != null ? 1 : 0);
				
				
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    
		return "exhibition/exhibitionUpdate";

	}
	
	
	
	
	@GetMapping("/exhibition/jsonExhibitionWrite") 
	public String jsonExhibitionWrite(
			Model model			
			) throws IOException {

			ClassPathResource resource = new ClassPathResource("data/json");
			String jsonSavePath = resource.getFile().getAbsolutePath();
			//String jsonSavePath = ".";		

			
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
	        	List<ExhibitionOldOld> itemsList = map.get("itemsList"); 

				ExhibitionOldOld exhibition = null;
				//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
				int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
				if (index < 0 || index >= itemsList.size()) {
					throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
				} else {
						exhibition = itemsList.get(index);
				}
				

	        	// 2) frontend로 전달: ExhibitionController.java에서 할일

				// 조회 결과를 request scope에 세팅 후 forward
				//model.addAttribute("exhibition", exhibition); //model : spring에서 사용하는 데이터 전달 객체 => js에서 이걸 받아 사용 (@PathVariable에 담긴 boardCode와 cp도 담겨져 넘어감)

				// 3) frontend로 전달:
				// 로그인 서비스 mock:		
				Member loginMember = new Member();
				loginMember.setMemberNickname("한국문화정보원");
				loginMember.setProfileImage("/images/member/penguin.jpeg"); 
				
				loginMember.setMemberNo(3); 
				
				model.addAttribute("loginMember", loginMember); 
	        	
				model.addAttribute("exhibitionCode", 3);  //boardCode === exhibitionCode === communityCode
				model.addAttribute("exhibitionName", "전시게시판");  //boardName === exhibitionName === communityName

	 			// 게시글 이미지가 있는 경우
	 			BoardImageOldOld thumbnail = null; 
	 			if(!exhibition.getImageList().isEmpty()) {
					
	 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
	 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
	 					thumbnail = exhibition.getImageList().get(0); // exhibitionImage 객체
	 				}
					

	 			}
				

	 			model.addAttribute("start", 0); 
				
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    
	    BoardImageOldOld boardImage = new BoardImageOldOld();
	    List<BoardImageOldOld> imageList = new ArrayList<>();
	    imageList.add(boardImage);
	    model.addAttribute("imageList", imageList);
	    
		return "exhibition/exhibitionWrite";
	}	
	
	
	@GetMapping("/board/exhibition/jsonExhibitionList")  
	public String jsonExhibitionListMoa(Model model) throws IOException {

        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; 
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
        
        if (inputStream == null) {
            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
        }

        log.info("타겟 파일 이름 : {}", targetFileName);
        
        List<ExhibitionOldOld> exhibitionDtoItems = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {

        	Map<String,  List<ExhibitionOldOld>> map = mapper.readValue(inputStream, 
        			new TypeReference<Map<String, List<ExhibitionOldOld>>>() {}
        	); 
        	
        	// 데이터 접근
        	List<ExhibitionOldOld> itemsList = map.get("itemsList"); 

        	Map<String, Object> mapExhibitionServiceImpl = new HashMap<>();
        	
        	int listCount = 209; 	// fixed 1 for mockSM230 json 데이터
        	int cp = 1; 			// fixed 1 for mockSM230 json 데이터
        	PaginationOldOld pagination = new PaginationOldOld(cp, listCount);
        	int paginationLimit = pagination.getLimit();
        	
        	// frontend로 전달: 
        	List<ExhibitionOldOld> itemsListPageLimit10 = new ArrayList<>(itemsList.subList(0, paginationLimit));
        	
			mapExhibitionServiceImpl.put("exhibitionList", itemsListPageLimit10);
			
        	mapExhibitionServiceImpl.put("pagination", pagination);
        				
			// 조회 결과를 request scope에 세팅 후 forward
			model.addAttribute("map", mapExhibitionServiceImpl); //model : spring에서 사용하는 데이터 전달 객체 => js에서 이걸 받아 사용 (@PathVariable에 담긴 boardCode와 cp도 담겨져 넘어감)

			// 로그인 서비스 mock:		
			Member loginMember = new Member();
			loginMember.setMemberNickname("한국문화정보원");
			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 
			
			loginMember.setMemberNo(3); // 임의할당 for testing (cf:  전시 exhibitionCode === boarcCode ===  communityCode = 3)
			
			model.addAttribute("loginMember", loginMember); // 
        	
        	// boardCode 값 mock으로 넘겨주기:
			model.addAttribute("exhibitionCode", 3);  //boardCode === exhibitionCode === communityCode
			model.addAttribute("exhibitionName", "전시게시판");  //boardName === exhibitionName === communityName
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
	    
		return "board/exhibition/exhibitionList";
	}
	


	@GetMapping("/board/exhibition/jsonExhibitionDetail")  
	public String jsonExhibitionDetailMoa(	Model model	) throws IOException {

        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; // targetFileName for API DTO -> Exhibition DTO mapping
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
        
        if (inputStream == null) {
            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
        }	

        log.info("타겟 파일 이름 : {}", targetFileName);
        
        List<ExhibitionOldOld> exhibitionDtoItems = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
        	Map<String,  List<ExhibitionOldOld>> map = mapper.readValue(inputStream, 
        			new TypeReference<Map<String, List<ExhibitionOldOld>>>() {}
        	); 
        	
        	List<ExhibitionOldOld> itemsList = map.get("itemsList");

			ExhibitionOldOld exhibition = null;
			//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
			int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
			if (index < 0 || index >= itemsList.size()) {
				throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
			} else {
					exhibition = itemsList.get(index);
			}


        	// frontend로 전달
			model.addAttribute("exhibition", exhibition); 
			
			// 로그인 서비스 mock:		
			Member loginMember = new Member();
			loginMember.setMemberNickname("한국문화정보원");
			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 
			loginMember.setMemberNo(3); 
			model.addAttribute("loginMember", loginMember); 
			
			// boardCode 값 mock
			model.addAttribute("exhibitionCode", 3);  
			model.addAttribute("exhibitionName", "전시게시판");  
			
 			BoardImageOldOld thumbnail = null; 
 			if(!exhibition.getImageList().isEmpty()) {
				
 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
 					thumbnail = exhibition.getImageList().get(0); // exhibitionImage 객체
 				}
				
 				model.addAttribute("thumbnail", thumbnail); 
						
 			}
			
			model.addAttribute("start", thumbnail != null ? 1 : 0); // 삼항 연산자
			
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
	    
		return "board/exhibition/exhibitionDetail";
	}


	
	@GetMapping("/board/exhibition/jsonExhibitionUpdate")  
	public String jsonExhibitionUpdateMoa( Model model ) throws IOException {

        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; // targetFileName for API DTO -> Exhibition DTO mapping
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
        
        if (inputStream == null) {
            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
        }	
        
        log.info("타겟 파일 이름 : {}", targetFileName);
        
        List<ExhibitionOldOld> exhibitionDtoItems = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
        	
        	Map<String,  List<ExhibitionOldOld>> map = mapper.readValue(inputStream, 
        			new TypeReference<Map<String, List<ExhibitionOldOld>>>() {}
        	); 
        	
        	List<ExhibitionOldOld> itemsList = map.get("itemsList"); 

			ExhibitionOldOld exhibition = null;
			//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
			int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
			if (index < 0 || index >= itemsList.size()) {
				throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
			} else {
					exhibition = itemsList.get(index);
			}
			

        	// frontend로 전달
			model.addAttribute("exhibition", exhibition); 
			
			// 로그인 서비스 mock:		
			Member loginMember = new Member();
			loginMember.setMemberNickname("한국문화정보원");
			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 			
			loginMember.setMemberNo(3); 
			model.addAttribute("loginMember", loginMember); 
			
        	// boardCode 값 mock
			model.addAttribute("exhibitionCode", 3);  
			model.addAttribute("exhibitionName", "전시게시판");  
			
 			// 게시글 이미지가 있는 경우
 			BoardImageOldOld thumbnail = null; 
 			if(!exhibition.getImageList().isEmpty()) {
				
 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
 					thumbnail = exhibition.getImageList().get(0); 
 				}
				
 				model.addAttribute("thumbnail", thumbnail); 
						
 			}
			

			model.addAttribute("start", thumbnail != null ? 1 : 0); 
			
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
	    
		return "board/exhibition/exhibitionUpdate";
		//return "main";
	}
	
	
	
	
	@GetMapping("/board/exhibition/jsonExhibitionWrite")  
	public String jsonExhibitionWriteMoa( Model model ) throws IOException {

        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; // targetFileName for API DTO -> Exhibition DTO mapping
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
        
        if (inputStream == null) {
            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
        }	
        
        log.info("타겟 파일 이름 : {}", targetFileName);
        
        
        List<ExhibitionOldOld> exhibitionDtoItems = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {

        	Map<String,  List<ExhibitionOldOld>> map = mapper.readValue(inputStream, 
        			new TypeReference<Map<String, List<ExhibitionOldOld>>>() {}
        	); 
        	
        	List<ExhibitionOldOld> itemsList = map.get("itemsList"); 

			ExhibitionOldOld exhibition = null;
			//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
			int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
			if (index < 0 || index >= itemsList.size()) {
				throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
			} else {
					exhibition = itemsList.get(index);
			}

        	// frontend로 전달
			// 로그인 서비스 mock:		
			Member loginMember = new Member();
			loginMember.setMemberNickname("한국문화정보원");
			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 
			loginMember.setMemberNo(3); 
			model.addAttribute("loginMember", loginMember); 
        	
        	// // boardCode 값 mock
			model.addAttribute("exhibitionCode", 3);  
			model.addAttribute("exhibitionName", "전시게시판");  
			

 			// 게시글 이미지가 있는 경우
 			BoardImageOldOld thumbnail = null; 
 			if(!exhibition.getImageList().isEmpty()) {
				
 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
 					thumbnail = exhibition.getImageList().get(0); 
 				}
					
 			}
			
 			model.addAttribute("start", 0); // for temporary check
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
	    
	    BoardImageOldOld boardImage = new BoardImageOldOld();
	    List<BoardImageOldOld> imageList = new ArrayList<>();
	    imageList.add(boardImage);
	    model.addAttribute("imageList", imageList);
	    
		return "board/exhibition/exhibitionWrite";
	}		
	
	
}
