console.log("main.js loaded ...... ");
// -----------------------------------------------------
// ----------------- API 요청/조회 ---------------------
// -----------------------------------------------------
// 1) 조회 버튼 클릭시
const selectBtn = document.getElementById("selectBtn");

selectBtn.addEventListener("click", () => {
    // 선택된 코드 snippet
    const selectQueryKey = document.getElementById("queryKey").value; // select-tag는 option-tag의 value, 없으면 option 

    // 
    console.log(Location.pathname);
    location.href= "http://localhost:8086/dbApiExhibition/retrieval";

})

