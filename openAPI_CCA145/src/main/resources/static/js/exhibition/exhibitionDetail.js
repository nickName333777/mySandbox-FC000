console.log("exhibitionDetail.js loaded...");

// // 하트 클릭 기능 위해 두 정보가 필요
// // 1. 로그인한 회원번호 -> 세션에 있다 (js에서는 서버 톰캣에 있는 session정보에 직접접근할 방법이 없다. ) -> 브라우저의 세션스토리지는 있지만, 이게 서버 세션정보는 아니다.
// // 2. 게시글 번호 -> jsp BOARD에 있다

// const exhibitionLike = document.getElementById("exhibitionLike");
// // 좋아요 버튼이 클릭 되었을 때

// // 로그인 x : "로그인 후 이용해 주세요"
// exhibitionLike.addEventListener("click", e => {
    
//     // 로그인 X
//     //if(loginMemberNo == null) { // 
//     if(loginMemberNo == "") { // loginMemberNo를 여기서 쓰려고 boardDetail.jsp에서 전역변수로 선언해놓았다 
//         alert("로그인 후 이용해 주세요");
//         return;   // 아래는 확인할 필요도 없다 ==> 이 다음에 코드들은 마치 else { } 구문의 코드 같은 역할을 하게 된다
//     }

//     let check; // 기존에 좋아요X(빈하트) : 0, 기존에 좋아요O     (꽉찬하트) : 1 ==> 두 경우 판별위한 변수
//                // 클래스목록 "fa-regular" 이 있는지 가지고 판단

//     // 이하 코드는 로그인 O
//     // contains("클래스명") : 클래스가 있으면 true, 없으면 false
//     if(e.target.classList.contains("fa-regular")){ // 좋아요X(빈하트)
//         check = 0;
//     } else { // 좋아요O (꽉찬하트)
//         check = 1;
//     }

//         // ajax로 서버에 제출할 파라미터를 모아둔 JS 객체
//     const data = {  memberNo : loginMemberNo,
//                     'boardNo': boardNo,
//                     "check"  : check
//                  };

//     // ajax 비동기 통신으로 요청 보낸다(GET:조회, POST: 조회가 아닌 다른 모든 방식)
//     fetch("/board/like", {
//             method : "POST",
//             headers : {"Content-Type" : "application/json"},
//             body : JSON.stringify(data) // JS객체 -> JSON
//     }) // @RequestBody로 body에 객체 전달
//     .then(resp => resp.text())  // 응답 객체를 필요한 형태로 파싱하여 리턴
//     .then(count => {
//             // 파싱된 데이터를 받아서 처리하는 코드 작성
//             console.log("count : " + count); // -1이면 SQL실패

//             // INSERT, DELETE실패 시 (좋아요 조회 실패시)
//             if (count == -1) {
//                 alert("좋아요 처리 실패 ^__^");
//                 return;
//             }
            

//             // 좋아요 성공했을 때 -> 알림메세지 보내야 함
//             // toggle() : 클래스가 있으면 없애고, 없으면 추가 (하트 클릭시 토글하도록 효과주기)
//             e.target.classList.toggle("fa-regular");
//             e.target.classList.toggle("fa-solid"); // 둘은 공존할 수 없다. (add, remove를 동시에 해주는 효과)

//             // 현재 게시글의 좋아요 수를 화면에 출력 (좋아요 조회 성공시)
//             e.target.nextElementSibling.innerText = count;

//             // (2025/09/19)

//             if (check==0) { // 기존에 좋아요 X 일경우(빈하트)
                
//                 // 게시글 작성자에게 알림보내기 (2025/09/19) // 좋아요 성공했을 때 -> 알림메세지 보내야 함 ==> sendNotification()의 전달 매개변수 type:" 좋아요"의 경우 처리
//                 sendNotification(
//                     // "boardLike", 
//                     "exhibitionLike", 
//                     location.pathname, // 게시글 상세 조회 페이지 주소
//                     boardNo, // 전역변수 boardNo
//                     `<strong>${memberNickname}</strong>님이 <strong>${exhibitTitle}</strong> 게시글을 좋아합니다.`
//                 );
                
//             }


//     })
//     .catch(err => {console.log(err)}) // 예외 발생 시 처리할 코드


// })