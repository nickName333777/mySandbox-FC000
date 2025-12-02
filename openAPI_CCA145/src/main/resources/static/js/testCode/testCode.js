console.log("testCode.js loaded ...... ");

// -----------------------------------------------------
// ------------ 자바 code snippet 테스트   -------------
// ------ CodeTest.java에서 code snippet 테스트 --------
// -----------------------------------------------------

// 1) select 태그에서 메뉴 선택시
const select = document.getElementById('selectTestCode');

select.addEventListener('change', function () {
    const url = this.value;
    console.log(url);
    console.log("url : " + url);
    console.log(url + " <- url");
    if (url) {
      window.location.href = url; // 또는 window.open(url, '_blank') 로 새 창 열기
    }
});


// 2) 테스트 버튼 클릭시
const selectBtn2 = document.getElementById("selectBtn2");

selectBtn2.addEventListener("click", () => {
    // 선택된 코드 snippet
    const urlSelectedCode = document.getElementById("selectTestCode").value; // select-tag는 option-tag의 value, 없으면 option 

    // 
    location.href= urlSelectedCode;

})