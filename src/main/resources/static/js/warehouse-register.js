// ========== WAREHOUSE REGISTER JAVASCRIPT ==========

let map, marker, geocoder;
let selectedImageFile = null;

// 페이지 로드 시 초기화
window.addEventListener('load', function() {
  console.log('✅ Warehouse register page loaded');

  if (typeof kakao !== 'undefined') {
    console.log('✅ Kakao Maps available');
    initMap();
    initAddressSearch();
  } else {
    console.error('❌ Kakao Maps not available');
  }

  initForm();
  initImageUpload();
});

// 지도 초기화
function initMap() {
  const container = document.getElementById('map');
  const options = {
    center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울 시청 기본 위치
    level: 3
  };

  map = new kakao.maps.Map(container, options);
  geocoder = new kakao.maps.services.Geocoder();

  console.log('✅ Map initialized');
}

// 주소 검색 초기화
function initAddressSearch() {
  const searchBtn = document.getElementById('searchAddressBtn');
  const addressInput = document.getElementById('address');

  searchBtn.addEventListener('click', function() {
    new daum.Postcode({
      oncomplete: function(data) {
        // 도로명 주소 또는 지번 주소 선택
        const fullAddress = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;

        // 주소 입력란에 표시
        addressInput.value = fullAddress;

        // 주소로 좌표 검색
        geocoder.addressSearch(fullAddress, function(result, status) {
          if (status === kakao.maps.services.Status.OK) {
            const coords = new kakao.maps.LatLng(result[0].y, result[0].x);

            // 지도 중심 이동
            map.setCenter(coords);

            // 기존 마커 제거
            if (marker) {
              marker.setMap(null);
            }

            // 새 마커 생성
            marker = new kakao.maps.Marker({
              map: map,
              position: coords
            });

            // 위도, 경도 hidden input에 저장
            document.getElementById('latitude').value = result[0].y;
            document.getElementById('longitude').value = result[0].x;

            console.log('✅ Address searched:', fullAddress);
            console.log('✅ Coordinates:', result[0].y, result[0].x);
          }
        });
      }
    }).open();
  });

  console.log('✅ Address search initialized');
}

// 폼 초기화 및 유효성 검사
function initForm() {
  const form = document.getElementById('warehouseRegisterForm');

  form.addEventListener('submit', async function(e) {
    e.preventDefault();

    // 유효성 검사
    if (!validateForm()) {
      return;
    }

    console.log('📦 Submitting warehouse data...');

    // API 호출
    try {
      const jwtToken = localStorage.getItem('jwtToken');

      if (!jwtToken) {
        alert('로그인이 필요합니다.');
        window.location.href = '/main/login';
        return;
      }

      // FormData 생성 (파일 업로드 포함)
      const formData = new FormData();

      // JSON 데이터를 Blob으로 변환
      const warehouseData = {
        title: document.getElementById('title').value.trim(),
        description: document.getElementById('description').value.trim(),
        address: document.getElementById('address').value.trim(),
        areaSqm: parseFloat(document.getElementById('areaSqm').value),
        pricePerMonth: parseFloat(document.getElementById('pricePerMonth').value),
        availableStatus: document.querySelector('input[name="availableStatus"]:checked').value === 'true',
        latitude: parseFloat(document.getElementById('latitude').value),
        longitude: parseFloat(document.getElementById('longitude').value)
      };

      formData.append('warehouseData', new Blob([JSON.stringify(warehouseData)], {
        type: 'application/json'
      }));

      // 이미지 파일 추가
      if (selectedImageFile) {
        formData.append('image', selectedImageFile);
        console.log('📷 Image attached:', selectedImageFile.name);
      }

      const response = await fetch('/api/warehouse/register', {
        method: 'POST',
        headers: {
          'Authorization': jwtToken
        },
        body: formData
      });

      if (response.ok) {
        const result = await response.json();
        console.log('✅ Warehouse registered:', result);
        alert('창고가 성공적으로 등록되었습니다!');
        // 캐시를 무시하고 페이지 이동
        window.location.replace('/main/location');
        // 페이지 완전 새로고침
        setTimeout(() => {
          window.location.reload(true);
        }, 100);
      } else {
        const error = await response.json();
        console.error('❌ Registration failed:', error);
        alert('창고 등록에 실패했습니다: ' + (error.message || '알 수 없는 오류'));
      }
    } catch (error) {
      console.error('❌ Network error:', error);
      alert('네트워크 오류가 발생했습니다. 다시 시도해주세요.');
    }
  });

  console.log('✅ Form initialized');
}

// 이미지 업로드 초기화
function initImageUpload() {
  const fileInput = document.getElementById('warehouseImage');
  const previewContainer = document.getElementById('imagePreviewContainer');

  // 파일 input 변경 이벤트
  fileInput.addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
      handleImageFile(file);
    }
  });

  // 클릭하여 파일 선택
  previewContainer.addEventListener('click', function(e) {
    if (!e.target.classList.contains('remove-image-btn')) {
      fileInput.click();
    }
  });

  // 드래그 앤 드롭
  previewContainer.addEventListener('dragover', function(e) {
    e.preventDefault();
    previewContainer.style.borderColor = '#2196f3';
    previewContainer.style.background = '#e3f2fd';
  });

  previewContainer.addEventListener('dragleave', function(e) {
    e.preventDefault();
    previewContainer.style.borderColor = '#e3f2fd';
    previewContainer.style.background = '#f8f9fa';
  });

  previewContainer.addEventListener('drop', function(e) {
    e.preventDefault();
    previewContainer.style.borderColor = '#e3f2fd';
    previewContainer.style.background = '#f8f9fa';

    const file = e.dataTransfer.files[0];
    if (file && file.type.startsWith('image/')) {
      handleImageFile(file);
    } else {
      alert('이미지 파일만 업로드 가능합니다.');
    }
  });

  console.log('✅ Image upload initialized');
}

// 이미지 파일 처리
function handleImageFile(file) {
  // 파일 크기 검증 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert('파일 크기는 5MB를 초과할 수 없습니다.');
    return;
  }

  // 이미지 타입 검증
  if (!file.type.startsWith('image/')) {
    alert('이미지 파일만 업로드 가능합니다.');
    return;
  }

  selectedImageFile = file;

  // 미리보기 표시
  const reader = new FileReader();
  reader.onload = function(e) {
    const previewContainer = document.getElementById('imagePreviewContainer');
    previewContainer.innerHTML = `
      <img src="${e.target.result}" alt="Preview" class="image-preview">
      <button type="button" class="remove-image-btn" onclick="removeImage()">×</button>
    `;
    previewContainer.classList.add('has-image');
  };
  reader.readAsDataURL(file);

  console.log('✅ Image selected:', file.name);
}

// 이미지 제거
function removeImage() {
  selectedImageFile = null;
  const fileInput = document.getElementById('warehouseImage');
  const previewContainer = document.getElementById('imagePreviewContainer');

  fileInput.value = '';
  previewContainer.classList.remove('has-image');
  previewContainer.innerHTML = `
    <div class="upload-placeholder">
      <span class="upload-icon">📷</span>
      <p>이미지를 선택하거나 드래그하세요</p>
      <span class="upload-hint">JPG, PNG, GIF (최대 5MB)</span>
    </div>
  `;

  console.log('✅ Image removed');
}

// 폼 유효성 검사
function validateForm() {
  const title = document.getElementById('title').value.trim();
  const address = document.getElementById('address').value.trim();
  const areaSqm = document.getElementById('areaSqm').value;
  const pricePerMonth = document.getElementById('pricePerMonth').value;
  const latitude = document.getElementById('latitude').value;

  if (!title) {
    alert('창고명을 입력해주세요.');
    document.getElementById('title').focus();
    return false;
  }

  if (!address) {
    alert('주소를 검색해주세요.');
    document.getElementById('searchAddressBtn').focus();
    return false;
  }

  if (!latitude) {
    alert('주소를 검색하여 지도에서 위치를 확인해주세요.');
    document.getElementById('searchAddressBtn').focus();
    return false;
  }

  if (!areaSqm || parseFloat(areaSqm) <= 0) {
    alert('올바른 면적을 입력해주세요.');
    document.getElementById('areaSqm').focus();
    return false;
  }

  if (!pricePerMonth || parseFloat(pricePerMonth) <= 0) {
    alert('올바른 월 임대료를 입력해주세요.');
    document.getElementById('pricePerMonth').focus();
    return false;
  }

  return true;
}