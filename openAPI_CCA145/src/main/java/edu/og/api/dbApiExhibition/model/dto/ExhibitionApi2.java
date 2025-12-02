package edu.og.api.dbApiExhibition.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ExhibitionApi2 {
	// DTO for API_CCA_145
	@JsonProperty("TITLE")
	private String title; // 제목 
	
	@JsonProperty("CNTC_INSTT_NM")
	private String institutionName;  // 연계기관명 ==> 뒤에 추최후원 컬럼삭제 않고 따로 사용 ==> 주최, 주관, 후원 중에 주관(/진행)
	
	@JsonProperty("COLLECTED_DATE")
	private String collectDate; // N; 수집일
	
	@JsonProperty("ISSUED_DATE")
	private String issueDate; // N; 자료생성일자
	
	@JsonProperty("DESCRIPTION")
	private String description; // 소개(설명)
	
	@JsonProperty("IMAGE_OBJECT")
	private String imageObject; // (??) 이미지주소
	
	@JsonProperty("LOCAL_ID")
	private String localId; // 전시ID
	
	@JsonProperty("URL")
	private String url; // N; 홈페이지주소
	
	@JsonProperty("VIEW_COUNT")
	private String viewCount; // N; 조회수
	
	@JsonProperty("SUB_DESCRIPTION")
	private String subDescriptioin; // N, 좌석정보
	
	@JsonProperty("SPATIAL_COVERAGE")
	private String spacialCoverage; // N, 예매안내
	
	@JsonProperty("EVENT_SITE")
	private String eventSite; // (??) 장소
	
	@JsonProperty("GENRE")
	private String genre; // (??)장르
	
	@JsonProperty("DURATION")
	private String duration; // N, 관람시간
	
	@JsonProperty("NUMBER_PAGES")
	private String numberPages; // N, 전시품(수)정보
	
	@JsonProperty("TABLE_OF_CONTENTS")
	private String tableOfContents; // N, 안내 및 유의사항
	
	@JsonProperty("AUTHOR")
	private String author; // 작가  (NOTE: 작자미상의 고미술/유물의 경우, author와 contactPoint가 같게 기재되는지 확인필요)=>테이블로 따로만들고, 작자미상경우 null 또는 '0' 

	@JsonProperty("CONTACT_POINT")
	private String contactPoint; // 문의 (NOTE: 작자미상의 고미술/유물의 경우, author와 contactPoint가 같게 기재되는지 확인필요)
	
	@JsonProperty("ACTOR")
	private String actor; // N, 출연진및제작진
	
	@JsonProperty("CONTRIBUTOR")
	private String contributor; //  주최/후원 ==> 앞에 institutionName과 다르게 사용 => 주최, 주관, 후원 중에 주최(/기획), 후원(/지원)
	
	@JsonProperty("AUDIENCE")
	private String audience; // ?? 연령 (NOTE: 명시된 것 없으면 contactPoint로 넘긴다)
	
	@JsonProperty("CHARGE")
	private String charge; // 관람료 (할인정보)
	
	@JsonProperty("PERIOD")
	private String period; // 기간 (NOTE 전시시작일 ~ 전시 종료일 parsing 해야함)

    @JsonProperty("EVENT_PERIOD") // N, 전시 관람에 드는 시간?
    private String eventPeriod;	

}
