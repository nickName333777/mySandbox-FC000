package edu.og.api.dbApiExhibition.model.exception;


//사용자 정의 예외 만들기
//-> Exception관련 클래스를 상속 받으면 된다.

//checked exception   : 예외 처리 필수
//unchecked exception : 예외 처리 선택 (개발자/사용자 실수)

//tip. unchecked exception을 만들고 싶은 경우 : RuntimeException 상속 받아서 구현
public class ApiRetrievalException extends RuntimeException{
	
	public ApiRetrievalException() { // ctrl + space-bar + enter: 기본 생성자
		//
		super("공공 데이터 API 조회중 예외 발생");
	}
	
	// 매개변수 생성자
	public ApiRetrievalException(String message) { // new ApiRetrievalException("merong~")
		super(message);
	}

}
