// package를 포함하라는 사용자 요청은 자바 코드에 해당되므로, JavaScript 코드에서는 생략합니다.
function execDaumPostcode() {
  new daum.Postcode({
    // oncomplete: 사용자가 검색 결과 항목을 클릭했을 때 실행되는 콜백 함수
    oncomplete: function(data) {
      // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분입니다.

      var roadAddr = data.roadAddress; // 도로명 주소
      var extraRoadAddr = ''; // 참고 항목

      // 법정동명이 있을 경우 추가합니다. (법정리는 제외)
      // 법정동의 경우 마지막 문자가 "동/로/가"로 끝납니다.
      if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
        extraRoadAddr += data.bname;
      }
      // 건물명이 있고, 공동주택일 경우 추가합니다.
      if(data.buildingName !== '' && data.apartment === 'Y'){
        extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
      }
      // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만듭니다.
      if(extraRoadAddr !== ''){
        extraRoadAddr = ' (' + extraRoadAddr + ')';
      }

      // 우편번호와 주소 정보를 해당 필드에 넣습니다.
      // document.getElementById('postcode').value = data.zonecode;

      // document.getElementById("jibunAddress").value = data.jibunAddress; // 지번 주소

      // 참고항목 필드에 넣습니다.
      // document.getElementById("extraAddress").value = extraRoadAddr;

      // 상세주소 필드에 포커스를 줍니다.
      if (selectedUserType === 'tenant') {
        document.getElementById("address").value = roadAddr;
        document.getElementById("detailedAddress").focus();
      } else {
        document.getElementById("warehouseAddress").value = roadAddr;
      }
    }
  }).open(); // 팝업창을 엽니다.
}