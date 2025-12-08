// ========== LOCATION PAGE JAVASCRIPT ==========

let warehouseData = [];
let map, markers = [], openInfoWindow = null, geocoder = null;

// 페이지 로드 시 초기화
window.addEventListener('load', async function() {
  console.log('✅ Location page loaded');

  if (typeof kakao !== 'undefined') {
    console.log('✅ Kakao Maps available');

    // 지도 먼저 초기화 (geocoder 생성)
    initMap();

    // 창고 데이터 로드
    await loadWarehouseData();

    // 데이터 로드 후 렌더링
    renderWarehouses(warehouseData);
    addMarkers(warehouseData);
    initFilters();
    initResetButton();
  } else {
    console.error('❌ Kakao Maps not available');
  }
});

// 창고 데이터 로드
async function loadWarehouseData() {
  try {
    console.log('📦 Loading warehouse data from API...');
    // 캐시를 무시하고 최신 데이터 가져오기
    const response = await fetch('/api/warehouse/list', {
      cache: 'no-cache',
      headers: {
        'Cache-Control': 'no-cache'
      }
    });

    if (response.ok) {
      const data = await response.json();
      console.log('✅ Warehouse data loaded:', data);
      console.log('📊 Total warehouses:', data.length);

      // API 데이터를 기존 형식으로 변환
      warehouseData = await Promise.all(data.map(async w => {
        let lat = w.latitude;
        let lng = w.longitude;

        // 위도/경도가 없으면 주소로 좌표 검색
        if (!lat || !lng) {
          console.log(`🔍 Geocoding address for: ${w.title}`);
          const coords = await geocodeAddress(w.address);
          if (coords) {
            lat = coords.lat;
            lng = coords.lng;
          } else {
            // 좌표를 찾지 못하면 서울 시청 기본 위치
            lat = 37.5665;
            lng = 126.9780;
          }
        }

        return {
          id: w.id,
          name: w.title,
          region: getRegionFromAddress(w.address),
          address: w.address,
          lat: lat,
          lng: lng,
          status: w.availableStatus ? 'available' : 'full',
          capacity: '알 수 없음',
          price: w.pricePerMonth || 0,
          features: ['CCTV', '안전관리'],
          owner: w.ownerName,
          image: w.imagePath || 'https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=400&h=250&fit=crop'
        };
      }));

      console.log('✅ Converted warehouse data:', warehouseData);

      // 데이터가 없으면 메시지 표시
      if (warehouseData.length === 0) {
        console.warn('⚠️ No warehouse data available');
        const container = document.getElementById('warehouseList');
        if (container) {
          container.innerHTML = '<div style="text-align:center; padding:40px; color:#666;">등록된 창고가 없습니다.</div>';
        }
      }
    } else {
      console.error('❌ Failed to load warehouse data - Status:', response.status);
      const errorText = await response.text();
      console.error('❌ Error response:', errorText);
    }
  } catch (error) {
    console.error('❌ Error loading warehouse data:', error);
  }
}

// 주소를 좌표로 변환하는 함수
function geocodeAddress(address) {
  return new Promise((resolve) => {
    if (!geocoder) {
      geocoder = new kakao.maps.services.Geocoder();
    }

    geocoder.addressSearch(address, function(result, status) {
      if (status === kakao.maps.services.Status.OK) {
        console.log(`✅ Geocoded: ${address} -> (${result[0].y}, ${result[0].x})`);
        resolve({
          lat: parseFloat(result[0].y),
          lng: parseFloat(result[0].x)
        });
      } else {
        console.warn(`⚠️ Failed to geocode: ${address}`);
        resolve(null);
      }
    });
  });
}

// 주소에서 지역 추출
function getRegionFromAddress(address) {
  if (address.includes('서울')) return 'seoul';
  if (address.includes('경기')) return 'gyeonggi';
  if (address.includes('인천')) return 'incheon';
  if (address.includes('부산')) return 'busan';
  if (address.includes('대구')) return 'daegu';
  return 'all';
}

// 지도 초기화
function initMap() {
  const container = document.getElementById('map');
  const options = {
    center: new kakao.maps.LatLng(37.5665, 126.9780),
    level: 8
  };

  map = new kakao.maps.Map(container, options);
  geocoder = new kakao.maps.services.Geocoder();
  console.log('✅ Map and geocoder initialized');
}

// 마커 추가
function addMarkers(warehouses) {
  // 기존 마커 제거
  markers.forEach(m => m.setMap(null));
  markers = [];
  
  warehouses.forEach(w => {
    const marker = new kakao.maps.Marker({
      position: new kakao.maps.LatLng(w.lat, w.lng),
      map: map
    });
    
    const infowindow = new kakao.maps.InfoWindow({
      content: '<div style="padding:0;min-width:200px;max-width:250px;border-radius:8px;overflow:hidden;box-shadow:0 4px 12px rgba(0,0,0,0.15);">' +
               '<img src="' + w.image + '" alt="' + w.name + '" style="width:100%;height:120px;object-fit:cover;display:block;">' +
               '<div style="padding:12px;background:#fff;">' +
               '<strong style="color:#1976d2;font-size:14px;display:block;margin-bottom:6px;">' + w.name + '</strong>' +
               '<div style="font-size:11px;color:#666;margin-bottom:4px;display:flex;align-items:center;gap:4px;">' +
               '<span>📍</span><span>' + w.address + '</span>' +
               '</div>' +
               '<div style="font-size:12px;color:#666;margin-bottom:6px;">임대인: ' + w.owner + '</div>' +
               '<div style="font-size:13px;color:#2196f3;font-weight:600;text-align:right;">' + w.price.toLocaleString() + '원/월</div>' +
               '</div>' +
               '</div>'
    });
    
    kakao.maps.event.addListener(marker, 'click', function() {
      // 이전에 열린 InfoWindow가 있으면 닫기
      if (openInfoWindow) {
        openInfoWindow.close();
      }
      infowindow.open(map, marker);
      openInfoWindow = infowindow;
      scrollToWarehouse(w.id);
    });
    
    markers.push(marker);
  });
  
  // 지도 범위 조정
  if (warehouses.length > 0) {
    const bounds = new kakao.maps.LatLngBounds();
    warehouses.forEach(w => {
      bounds.extend(new kakao.maps.LatLng(w.lat, w.lng));
    });
    map.setBounds(bounds);
  }
  
  console.log('✅ Added ' + warehouses.length + ' markers');
}

// 창고 목록 렌더링
function renderWarehouses(warehouses) {
  const container = document.getElementById('warehouseList');
  const countElement = document.getElementById('warehouseCount');
  
  countElement.textContent = warehouses.length;
  
  container.innerHTML = warehouses.map(w => 
    '<div class="warehouse-card" data-id="' + w.id + '">' +
      '<img src="' + w.image + '" alt="' + w.name + '" class="warehouse-image">' +
      '<div class="warehouse-content">' +
        '<h3 class="warehouse-name">' +
          w.name +
          '<span class="warehouse-badge badge-' + w.status + '">' +
            getStatusText(w.status) +
          '</span>' +
        '</h3>' +
        '<div class="warehouse-info">' +
          '<div class="info-row">' +
            '<span class="info-icon">📍</span>' +
            '<span>' + w.address + '</span>' +
          '</div>' +
          '<div class="info-row">' +
            '<span class="info-icon">👤</span>' +
            '<span>임대인: ' + w.owner + '</span>' +
          '</div>' +
          '<div class="info-row">' +
            '<span class="info-icon">📊</span>' +
            '<span>현재 이용률: ' + w.capacity + '</span>' +
          '</div>' +
        '</div>' +
        '<div class="warehouse-features">' +
          w.features.map(f => '<span class="feature-tag">' + f + '</span>').join('') +
        '</div>' +
        '<div class="warehouse-price">' +
          '<div class="price-amount">' +
            w.price.toLocaleString() + '<span>원/월</span>' +
          '</div>' +
          '<button class="btn-detail" onclick="showWarehouseDetail(' + w.id + ')">자세히 보기</button>' +
        '</div>' +
      '</div>' +
    '</div>'
  ).join('');
  
  console.log('✅ Rendered ' + warehouses.length + ' warehouses');
}

// 상태 텍스트 반환
function getStatusText(status) {
  const statusMap = {
    'available': '예약가능',
    'limited': '잔여적음',
    'full': '예약마감'
  };
  return statusMap[status] || '확인중';
}

// 필터 초기화
function initFilters() {
  document.querySelectorAll('.filter-btn').forEach(btn => {
    btn.addEventListener('click', function() {
      document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
      this.classList.add('active');
      
      const region = this.dataset.region;
      const filtered = region === 'all' 
        ? warehouseData 
        : warehouseData.filter(w => w.region === region);
      
      renderWarehouses(filtered);
      addMarkers(filtered);
    });
  });
  
  console.log('✅ Filters initialized');
}

// 창고 상세 페이지로 이동
function showWarehouseDetail(id) {
  const w = warehouseData.find(w => w.id === id);
  if (w) {
    alert(w.name + ' 상세 페이지로 이동합니다.\n(상세 페이지 작업 진행중)');
    // 실제 구현: window.location.href = '/warehouse/' + id;
  }
}

// 창고 카드로 스크롤
function scrollToWarehouse(id) {
  const card = document.querySelector('[data-id="' + id + '"]');
  if (card) {
    card.scrollIntoView({ behavior: 'smooth', block: 'center' });
    card.style.animation = 'highlight 1s ease';
  }
}

// 초기화 버튼 기능
function initResetButton() {
  const resetBtn = document.getElementById('resetMapBtn');
  if (resetBtn) {
    resetBtn.addEventListener('click', function() {
      // InfoWindow 닫기
      if (openInfoWindow) {
        openInfoWindow.close();
        openInfoWindow = null;
      }

      // 필터를 "전체"로 리셋
      document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
      document.querySelector('[data-region="all"]').classList.add('active');

      // 전체 창고 표시
      renderWarehouses(warehouseData);
      addMarkers(warehouseData);

      console.log('✅ Map reset');
    });
  }
}
