// Image Slider
let currentSlide = 0;
let slides = [];
let indicators = [];
let totalSlides = 0;
let slideInterval;

// 헤더 버튼 요소
const headerBtn = document.querySelector(".header-buttons");

document.addEventListener("DOMContentLoaded", () => {
  const userName = localStorage.getItem("name");
  const userRoles = JSON.parse(localStorage.getItem("userRoles") || "[]");

  // 헤더 버튼 초기화
  if (headerBtn && userName) {
    let str = "";
    str += `<a href="${mypageUrl}" class="btn-header">안녕하세요. ${userName}님</a>`;
    str += `<a onclick="showLogoutModal();" class="btn-header btn-signup">로그아웃</a>`;
    headerBtn.innerHTML = str;
  }

  // 임대인(OWNER) 역할이 있으면 창고 등록 메뉴 표시
  const ownerMenu = document.getElementById("ownerMenu");
  if (ownerMenu && userRoles.includes("OWNER")) {
    ownerMenu.style.display = "block";
  }

  // 슬라이더 초기화 (메인 페이지에만 존재)
  initializeSlider();

  // 모달 초기화 - 모든 모달을 숨김 상태로 설정
  const logoutModal = document.getElementById("logoutModal");
  const logoutSuccessModal = document.getElementById("logoutSuccessModal");
  
  if (logoutModal) {
    logoutModal.classList.remove("active");
    // 로그아웃 확인 모달 외부 클릭 처리
    logoutModal.addEventListener("click", (e) => {
      if (e.target === logoutModal) {
        closeLogoutModal();
      }
    });
  }
  
  if (logoutSuccessModal) {
    logoutSuccessModal.classList.remove("active");
    // ESC 키로 모달 닫기
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") {
        closeLogoutModal();
      }
    });
  }
});

// 슬라이더 초기화 함수
function initializeSlider() {
  // 슬라이더 요소들이 존재하는지 확인
  const slideElements = document.querySelectorAll(".slide");
  const indicatorElements = document.querySelectorAll(".indicator");
  
  // 슬라이더 요소가 존재할 때만 초기화
  if (slideElements.length > 0 && indicatorElements.length > 0) {
    slides = slideElements;
    indicators = indicatorElements;
    totalSlides = slides.length;
    
    console.log(`슬라이더 초기화 완료: ${totalSlides}개의 슬라이드`);
    
    // 첫 번째 슬라이드 활성화
    if (totalSlides > 0) {
      showSlide(0);
      // 자동 슬라이드 시작 (슬라이드가 2개 이상일 때만)
      if (totalSlides > 1) {
        slideInterval = setInterval(autoSlide, 10000);
      }
    }
  } else {
    console.log('슬라이더 요소를 찾을 수 없습니다. 슬라이더를 건너뜁니다.');
  }
}

// 로그아웃 모달 표시
function showLogoutModal() {
  const userName = localStorage.getItem("name");
  const logoutModal = document.getElementById("logoutModal");
  const logoutUserNameSpan = document.getElementById("logoutUserName");
  
  if (!logoutModal) {
    console.error("로그아웃 모달을 찾을 수 없습니다.");
    return;
  }
  
  if (userName && logoutUserNameSpan) {
    logoutUserNameSpan.textContent = userName;
  }
  
  // 모달 표시
  logoutModal.classList.add("active");
  
  // 바디 스크롤 방지
  document.body.style.overflow = "hidden";
}

// 로그아웃 모달 닫기
function closeLogoutModal() {
  const logoutModal = document.getElementById("logoutModal");
  
  if (logoutModal) {
    logoutModal.classList.remove("active");
  }
  
  // 바디 스크롤 복원
  document.body.style.overflow = "";
}

// 로그아웃 확인
function confirmLogout() {
  logout(logoutUrl);
}

// 로그아웃 성공 모달 표시
function showLogoutSuccessModal() {
  const userName = localStorage.getItem("name");
  const logoutSuccessModal = document.getElementById("logoutSuccessModal");
  const logoutSuccessUserNameSpan = document.getElementById("logoutSuccessUserName");
  
  // 확인 모달 닫기
  closeLogoutModal();
  
  if (!logoutSuccessModal) {
    console.error("로그아웃 성공 모달을 찾을 수 없습니다.");
    // 모달이 없어도 로그아웃은 진행
    localStorage.clear();
    window.location.href = mainUrl;
    return;
  }
  
  if (userName && logoutSuccessUserNameSpan) {
    logoutSuccessUserNameSpan.textContent = userName;
  }
  
  logoutSuccessModal.classList.add("active");
  
  // 바디 스크롤 방지
  document.body.style.overflow = "hidden";
  
  // 3초 후 자동으로 닫기 (선택사항)
  setTimeout(() => {
    closeLogoutSuccessModal();
  }, 3000);
}

// 로그아웃 성공 모달 닫기 및 페이지 이동
function closeLogoutSuccessModal() {
  const logoutSuccessModal = document.getElementById("logoutSuccessModal");
  
  if (logoutSuccessModal) {
    logoutSuccessModal.classList.remove("active");
  }
  
  // 바디 스크롤 복원
  document.body.style.overflow = "";
  
  // localStorage 클리어하고 메인 페이지로 이동
  localStorage.clear();
  window.location.href = mainUrl;
}

function logout(url) {
  fetch(url, {
    method: "POST",
    headers: {
      "Authorization": localStorage.getItem("jwtToken")
    }
  })
  .then(response => {
    if (response.ok) {
      // 로그아웃 성공 모달 표시
      showLogoutSuccessModal();
    } else {
      console.error("로그아웃 실패");
      // 실패해도 로컬스토리지는 클리어하고 메인으로 이동
      localStorage.clear();
      window.location.href = mainUrl;
    }
  })
  .catch(error => {
    console.error("로그아웃 오류:", error);
    // 오류 발생해도 로컬스토리지는 클리어하고 메인으로 이동
    localStorage.clear();
    window.location.href = mainUrl;
  });
}

function showSlide(index) {
  // 슬라이더 요소들이 존재하는지 확인
  if (!slides || slides.length === 0 || !indicators || indicators.length === 0) {
    console.warn('슬라이더 요소를 찾을 수 없어 showSlide를 건너뜁니다.');
    return;
  }
  
  // 인덱스 유효성 검사
  if (index < 0 || index >= totalSlides) {
    console.warn(`잘못된 슬라이드 인덱스: ${index}`);
    return;
  }
  
  try {
    // Remove active class from all slides and indicators
    slides.forEach((slide) => {
      if (slide && slide.classList) {
        slide.classList.remove("active");
      }
    });
    
    indicators.forEach((indicator) => {
      if (indicator && indicator.classList) {
        indicator.classList.remove("active");
      }
    });

    // Add active class to current slide and indicator
    if (slides[index] && slides[index].classList) {
      slides[index].classList.add("active");
    }
    
    if (indicators[index] && indicators[index].classList) {
      indicators[index].classList.add("active");
    }
    
  } catch (error) {
    console.error('showSlide 실행 중 오류:', error);
  }
}

function changeSlide(direction) {
  // 슬라이더가 초기화되지 않았으면 실행하지 않음
  if (totalSlides === 0) {
    console.warn('슬라이더가 초기화되지 않아 changeSlide를 건너뜁니다.');
    return;
  }
  
  currentSlide += direction;

  if (currentSlide >= totalSlides) {
    currentSlide = 0;
  } else if (currentSlide < 0) {
    currentSlide = totalSlides - 1;
  }

  showSlide(currentSlide);
  resetInterval();
}

function goToSlide(index) {
  // 슬라이더가 초기화되지 않았으면 실행하지 않음
  if (totalSlides === 0) {
    console.warn('슬라이더가 초기화되지 않아 goToSlide를 건너뜁니다.');
    return;
  }
  
  currentSlide = index;
  showSlide(currentSlide);
  resetInterval();
}

function autoSlide() {
  // 슬라이더가 초기화되지 않았으면 실행하지 않음
  if (totalSlides === 0) {
    return;
  }
  
  currentSlide++;
  if (currentSlide >= totalSlides) {
    currentSlide = 0;
  }
  showSlide(currentSlide);
}

function resetInterval() {
  if (slideInterval) {
    clearInterval(slideInterval);
  }
  
  // 슬라이드가 2개 이상일 때만 자동 슬라이드 시작
  if (totalSlides > 1) {
    slideInterval = setInterval(autoSlide, 10000); // 10초마다 자동 슬라이드
  }
}

// Modal functions
function openModal(type) {
  if (type === "login") {
    const loginModal = document.getElementById("loginModal");
    if (loginModal) {
      loginModal.classList.add("active");
    }
  } else if (type === "signup") {
    const signupModal = document.getElementById("signupModal");
    if (signupModal) {
      signupModal.classList.add("active");
    }
  }
}

function closeModal(type) {
  if (type === "login") {
    const loginModal = document.getElementById("loginModal");
    if (loginModal) {
      loginModal.classList.remove("active");
    }
  } else if (type === "signup") {
    const signupModal = document.getElementById("signupModal");
    if (signupModal) {
      signupModal.classList.remove("active");
    }
  }
}

function switchModal(type) {
  if (type === "login") {
    const signupModal = document.getElementById("signupModal");
    const loginModal = document.getElementById("loginModal");
    if (signupModal) signupModal.classList.remove("active");
    if (loginModal) loginModal.classList.add("active");
  } else if (type === "signup") {
    const loginModal = document.getElementById("loginModal");
    const signupModal = document.getElementById("signupModal");
    if (loginModal) loginModal.classList.remove("active");
    if (signupModal) signupModal.classList.add("active");
  }
}

// Smooth scroll
document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
  anchor.addEventListener("click", function (e) {
    e.preventDefault();
    const target = document.querySelector(this.getAttribute("href"));
    if (target) {
      target.scrollIntoView({
        behavior: "smooth",
      });
    }
  });
});

// Header scroll effect
window.addEventListener("scroll", () => {
  const header = document.querySelector(".header");
  if (header) {
    if (window.scrollY > 100) {
      header.style.boxShadow = "0 2px 20px rgba(0,0,0,0.1)";
    } else {
      header.style.boxShadow = "0 2px 10px rgba(0,0,0,0.05)";
    }
  }
});

// 페이지 언로드 시 인터벌 정리
window.addEventListener("beforeunload", () => {
  if (slideInterval) {
    clearInterval(slideInterval);
  }
});