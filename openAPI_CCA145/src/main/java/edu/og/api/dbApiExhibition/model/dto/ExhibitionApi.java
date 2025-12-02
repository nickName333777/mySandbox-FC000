package edu.og.api.dbApiExhibition.model.dto;

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
public class ExhibitionApi {
	// DTO for API_CCA_145
	private String TITLE; // title; // 제목 
	private String CNTC_INSTT_NM; // institutionName;  // 연계기관명 ==> 뒤에 추최후원 컬럼삭제 않고 따로 사용 ==> 주최, 주관, 후원 중에 ==> 주관(/진행)
	private String COLLECTED_DATE; // collectDate; // N; 수집일
	private String ISSUED_DATE; // issueDate; // N; 자료생성일자
	private String DESCRIPTION; // description; // 소개(설명)
	private String IMAGE_OBJECT; // imageObject; // (??) 이미지주소
	private String LOCAL_ID; // localId; // 전시ID
	private String URL; // url; // N; 홈페이지주소
	private String VIEW_COUNT; //viewCount; // N; 조회수
	private String SUB_DESCRIPTION; // subDescriptioin; // N, 좌석정보
	private String SPATIAL_COVERAGE; // spacialCoverage; // N, 예매안내
	private String EVENT_SITE; // eventSite; // (??) 장소
	private String GENRE; // genre; // (??)장르
	private String DURATION; // duration; // N, 관람시간
	private String NUMBER_PAGES; // numberPages; // N, 전시품(수)정보
	private String TABLE_OF_CONTENTS; // tableOfContents; // N, 안내 및 유의사항
	private String AUTHOR; // author; // 작가  (NOTE: 작자미상의 고미술/유물의 경우, author와 contactPoint가 같게 기재되는지 확인필요)=>테이블로 따로만들고, 작자미상경우 null 또는 '0' 
	private String CONTACT_POINT; // contactPoint; // 문의 (NOTE: 작자미상의 고미술/유물의 경우, author와 contactPoint가 같게 기재되는지 확인필요)
	private String ACTOR; // actor; // N, 출연진및제작진
	private String CONTRIBUTOR; // contribution; //  주최/후원 ==> 앞에 institutionName과 다르게 사용 => 주최, 주관, 후원 중에 ==> 주최(/기획) / 후원(/지원) ==> '/'으로 주최/후원 구분
	private String AUDIENCE; // audience; // ?? 연령 (NOTE: 명시된 것 없으면 contactPoint로 넘긴다)
	private String CHARGE; // charge; // 관람료 (할인정보)
	private String PERIOD; // period; // 기간 (NOTE 전시시작일 ~ 전시 종료일 parsing 해야함)
	private String EVENT_PERIOD; // eventPeriod; // 시간
	
	

}
