# 전시공공 데이터 API 수집

동기 : MoA 공연/전시 게시판 프로젝트에서 사용할 전시 데이터를 수집 <br>


앱 실행: <br>
- 요청주소 "http://localhost:8086/"를 주소창에 입력 <br>
- 메인화면에서 "조회"버튼을 누르면 300 건의 전시데이터를 json형식으로 수집 <br>
- API key 필요: 문화 공공데이터 광장 페이지에서 API 활용신청 해야함 (https://www.culture.go.kr/data/openapi/openapiView.do?id=598&category=I&gubun=B#/default/%EC%9A%94%EC%B2%AD%EB%A9%94%EC%8B%9C%EC%A7%80%20Get) <br>

< 메인 화면 예시: 전시데이터 조회 페이지 (게시물 300건) > <br>
<img width="590" height="506" alt="kcisa 문화 공공데이터 API 활용 - Google Chrome_002" src="https://github.com/user-attachments/assets/a86c3fae-493e-422d-be8c-3002c4766c19" /> <br>


<br>


< 예시: 전시 DB생성을 위해 Json으로 수집한 API 공공 데이터를 1차 가공/변환 > <br> 
<img width="590" height="506" alt="kcisa 문화 공공데이터 API 활용 - Google Chrome_003" src="https://github.com/user-attachments/assets/c3a9c15a-8b75-4788-b2ed-dc16c99cbadb" />

- 옵션: loading MockSM209 =>  209건의 API 전시데이터를 로딩하여 화면에서 확인<br>
- 옵션: API2DTO, API2DTO_rev => 209건의 API 전시데이터를 DTO 형식으로 가공/저장<br>
