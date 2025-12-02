package edu.og.api.exhibition.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionOldOld {
	
	// 0) DTO for our purpose
	private int exhibitNo; // BOARD_No, boardNo (게시판 글번호; PK)
	private String exhibitUpdateDate;
	private int readCount; // 게시글 조회수
	//private char delFlag; // 게시글 삭제 flag
	//private int memberNo; // FK ??
	// private int boardCode;	// FK ?? 

	// 1) BOARDTYPE JOIN
	private int communityCode;
	
	// 2) 서브쿼리
	private int likeCount; // 좋아요 수
	
	// 3) 회원 JOIN
	private String memberNickname; 
	private int memberNo;
	private String profileImage;
	private String thumbnail;

	// 이미지 목록은 board-mapper.xml에서 collection으로 정의해주어야 하며,
	// 이때 select="selectImageList" 와 같은 설정으로 DB조회해서 결과를 EXHIBITION DTO에 담아주게된다(실제 imageList에 대한 DB조회가 이때 일어나고,
	//  DAO에서는 selectExhibition()메소드 호출만으로 exhibition, imageList DTO의 DB 조회(select)를 각각 실행하는 것이된다. )
	// 이미지 목록
	private List<BoardImageOldOld> imageList;
	
	
	// 4) EXHIBITION JOIN
	//
	// DTO fields만들기 위해 API_CCA_145 에서 가져와야할 필드들 
	// DTO 필드									 	// API DTO 필드(JSON-key)				// ORACLE DATABASE FIELD	
	private String exhibitTitle; // 게시판 타이틀		title ("TITLE")							"BOARD_TITLE" 				==> NOT-NULL
	private String exhibitCreateDate; //			collectDate ("COLLECTED_DATE")			"B_CREATE_DATE"				==> NOT-NULL
	private String exhibitContent; // 게시판 글내용	description ("DESCRIPTION")				"BOARD_CONTENT" 			==> NOT-NULL (imputation needed)
	
	private String exhibitImgObject; // 			imageObject	("IMAGE_OBJECT")			"IMG_PATH", "IMG_ORIG" (IMAGE_OBJECT = IMG_PATH/IMG_ORIG)
	
	private String exhibitSubTitle; // 				subDescription ("SUB_DESCRIPTION")		"EXHIBIT_SUB_TITLE"
	private String exhibitDate; // 					period ("PERIOD") 						"EXHIBIT_DATE"
	private String exhibitLocation; // 				eventSite ("EVENT_SITE")				"EXHIBIT_LOCATION"
	private String exhibitGenre; // 				genre ("GENRE")							"EXHIBIT_GENRE"
	private String exhibitContact; // 				contactPoint ("CONTACT_POINT")			"EXHIBIT_CONTACT"			==> NOT-NULL (imputation needed) 
	private String exhibitAudience; // 				audience ("AUDIENCE")					"EXHIBIT_AUDIENCE"
	private String exhibitCharge; // 				charge ("CHARGE")						"EXHIBIT_CHARGE"			==> NOT-NULL (imputation needed)
	
	// AUTHOR 테이블 JOIN
	private String exhibitAuthor; // 				author ("AUTHOR")						"AUTHOR_NAME"
	
	// INSTITUTION 테이블 JOIN
	private String exhibitInstitution;  // 			institutionName ("CNTC_INSTT_NM")		"EXHIBIT_INST_NAME", 		==> NOT-NULL
										//													("EXHIBIT_INST_TEL" -> 22개 기관에 문의전화번호 수동수집) 	==> NOT-NULL
	 
	// CONTRIBUTOR 테이블 JOIN
	private String exhibitContributor; // 			contributor ("CONTRIBUTOR")				"EXHIBIT_HOST" and "EXHIBIT_SUPPORT"
	
	
	
//	// DTO fields만들기 위해 API_CCA_145 에서 가져와야할 필드들 
//	private String title; // 제목 
//	private String institutionName;	
//	private String description; // 소개(설명)
	
//	private String imageObject; // (??) 이미지주소

//	private String localId; // 전시ID (==> ?? InstitutionName과 연관되어 있는거??)
//
//	private String eventSite; // (??) 장소 ex) 제2전시실
//	private String genre; // (??) 장르 (NOTE:업으면 제목보고 임의로 채워 넣던지, AI로 찾아 missing value imputation?)
//	private String author; // 작가  (NOTE: 작자미상의 고미술/유물의 경우, author와 contactPoint가 같게 기재되는지 확인필요)
//	private String contactPoint; // 문의 (NOTE: 작자미상의 고미술/유물의 경우, author와 contactPoint가 같게 기재되는지 확인필요)
//	private String contribution; //  후원 ==> 앞에 institutionName과 다르게 사용 ==>만약 후원자 없으면 전시기관(institutionName)을 후원자로...
//	private String audience; // ?? 연령 (NOTE: 명시된 것 없으면 contactPoint로 넘긴다)
//	private String charge; // 관람료 (할인정보)
//	private String period; // 기간 (NOTE 전시시작일 ~ 전시 종료일 parsing 해야함)

	
	
	

}
