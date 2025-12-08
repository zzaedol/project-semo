# 🗺️ Kakao Map 문제 해결 가이드

## ✅ 수정 완료 사항

코드에 다음 개선사항을 적용했습니다:
- ✅ 스크립트 로딩 개선 (`autoload=false` 추가)
- ✅ 상세한 콘솔 로그 추가
- ✅ 에러 핸들링 강화
- ✅ 재시도 로직 추가

---

## 🔍 문제 진단 방법

### **Step 1: 브라우저 콘솔 확인**

1. 페이지 접속: `http://localhost:8080/main/location`
2. **F12** 키를 눌러 개발자 도구 열기
3. **Console** 탭 확인

**정상적인 경우:**
```
Script loaded - kakao: object
DOM loaded, initializing map...
Kakao Maps loaded successfully
Initializing map...
Map created successfully
Adding markers for 12 warehouses
Map bounds set successfully
```

**에러가 있는 경우:** 아래 에러별 해결 방법 참조

---

## 🛠️ 에러별 해결 방법

### **에러 1: Invalid API key**
```
Error: [ERROR] Invalid appkey
```

**원인:** API 키가 잘못되었거나 만료됨

**해결 방법:**
1. Kakao Developers 콘솔 접속
2. 앱 선택 → "앱 키" 탭
3. **JavaScript 키** 복사 (REST API 키가 아님!)
4. `location.html` 파일 수정:
```html
<script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=여기에_JavaScript_키_붙여넣기&autoload=false"></script>
```

---

### **에러 2: Refused to display**
```
Refused to display 'https://dapi.kakao.com/...' in a frame because it set 'X-Frame-Options' to 'deny'
```

**원인:** 플랫폼이 등록되지 않음

**해결 방법:**
1. Kakao Developers 콘솔
2. 앱 선택 → **"플랫폼"** 탭 클릭
3. "Web 플랫폼 등록" 클릭
4. 다음 URL을 **모두** 등록:
```
http://localhost:8080
http://127.0.0.1:8080
```
5. 저장 후 **브라우저 새로고침** (Ctrl + F5)

---

### **에러 3: kakao is not defined**
```
Uncaught ReferenceError: kakao is not defined
```

**원인:** 스크립트가 로드되기 전에 코드 실행

**해결 방법:**
- 이미 수정된 코드에서 해결됨
- 브라우저 캐시 삭제:
  - **Ctrl + Shift + Delete**
  - "캐시된 이미지 및 파일" 체크
  - 삭제 후 새로고침

---

### **에러 4: Mixed Content**
```
Mixed Content: The page at 'https://...' was loaded over HTTPS, but requested an insecure resource
```

**원인:** HTTPS 페이지에서 HTTP 리소스 로드 시도

**해결 방법:**
스크립트 URL을 프로토콜 상대 경로로 사용 (이미 적용됨):
```html
<!-- ✅ 올바름 -->
<script src="//dapi.kakao.com/..."></script>

<!-- ❌ 잘못됨 -->
<script src="http://dapi.kakao.com/..."></script>
```

---

### **에러 5: 지도는 나오지만 마커가 안 보임**
```
Map created successfully
Adding markers for 12 warehouses
```

**원인:** 좌표 데이터 문제 또는 줌 레벨 문제

**해결 방법:**
1. 콘솔에서 에러 확인
2. 지도를 수동으로 축소해보기
3. 좌표 데이터 확인

---

## 📋 완전한 체크리스트

### **Kakao Developers 설정**
- [ ] 앱이 생성되어 있음
- [ ] JavaScript 키를 복사함
- [ ] 플랫폼 탭에서 Web 플랫폼 등록
- [ ] `http://localhost:8080` 등록
- [ ] `http://127.0.0.1:8080` 등록
- [ ] 저장 완료

### **코드 설정**
- [ ] location.html에 올바른 API 키 입력
- [ ] `&autoload=false` 파라미터 포함
- [ ] 브라우저 캐시 삭제
- [ ] 서버 재시작

### **브라우저 확인**
- [ ] F12 콘솔에 에러 없음
- [ ] "Map created successfully" 로그 확인
- [ ] 지도가 화면에 표시됨
- [ ] 마커 12개가 보임

---

## 🎯 빠른 테스트 방법

### **1. API 키 유효성 테스트**
브라우저 주소창에 다음 URL 입력:
```
https://dapi.kakao.com/v2/maps/sdk.js?appkey=여기에_당신의_키
```

**성공:** JavaScript 코드가 표시됨  
**실패:** 에러 메시지 또는 빈 페이지

### **2. 간단한 HTML 테스트**
다음 HTML을 만들어 테스트:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Kakao Map Test</title>
</head>
<body>
    <div id="map" style="width:100%;height:400px;"></div>
    
    <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=여기에_당신의_키"></script>
    <script>
        var container = document.getElementById('map');
        var options = {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 3
        };
        var map = new kakao.maps.Map(container, options);
        console.log('Map created:', map);
    </script>
</body>
</html>
```

이 파일을 저장하고 브라우저에서 열어보세요.

---

## 🔧 고급 디버깅

### **네트워크 탭 확인**
1. F12 → **Network** 탭
2. 페이지 새로고침
3. `sdk.js` 검색
4. 상태 코드 확인:
   - **200**: 정상 로드
   - **403**: 권한 없음 (플랫폼 미등록)
   - **404**: URL 오류

### **Console에서 직접 테스트**
F12 Console에서 다음 코드 실행:

```javascript
// Kakao 객체 확인
console.log(kakao);

// 지도 객체 확인
console.log(map);

// 마커 개수 확인
console.log(markers.length);

// 수동으로 마커 추가 테스트
const testMarker = new kakao.maps.Marker({
    position: new kakao.maps.LatLng(37.5665, 126.9780),
    map: map
});
```

---

## 💡 자주 하는 실수

### **1. REST API 키 vs JavaScript 키**
❌ **잘못:** REST API 키 사용  
✅ **올바름:** JavaScript 키 사용

Kakao Developers 콘솔에서 **"앱 키"** 탭:
- REST API 키: `xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx` (길고 복잡)
- **JavaScript 키**: `xxxxxxxxxxxxxxxx` (상대적으로 짧음) ← 이것을 사용!

### **2. 플랫폼 등록 누락**
가장 흔한 원인! 반드시 Web 플랫폼 등록 필요

### **3. 브라우저 캐시**
설정을 바꿨는데도 작동 안 하면 → **Ctrl + Shift + Delete**

### **4. localhost vs 127.0.0.1**
둘 다 등록해야 함! 하나만 등록하면 다른 주소에서 안 됨

---

## 📞 여전히 안 되나요?

다음 정보를 확인해주세요:

1. **브라우저 콘솔의 에러 메시지** (F12)
2. **사용 중인 API 키** (앞 4자리만)
3. **등록한 플랫폼 URL** 목록
4. **브라우저 종류 및 버전**

이 정보를 알려주시면 정확한 해결 방법을 드리겠습니다!

---

## ✅ 정상 작동 확인

지도가 제대로 작동하면:
- ✅ 지도가 화면에 표시됨
- ✅ 12개 마커가 보임
- ✅ 마커 클릭 시 인포윈도우 표시
- ✅ 필터 버튼으로 지역 변경 가능
- ✅ 콘솔에 에러 없음

**축하합니다! 🎉**
