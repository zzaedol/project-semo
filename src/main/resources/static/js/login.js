document.addEventListener('DOMContentLoaded', () => {

  // 💡 페이지 로드 시 로그인 상태 확인
  const jwtToken = localStorage.getItem('jwtToken');
  if (jwtToken) {
    // 토큰이 존재하면, 즉 로그인 상태이면
    // 사용자의 역할에 따라 적절한 페이지로 리다이렉션
    // const userRoles = JSON.parse(localStorage.getItem('userRoles') || '[]');
    // if (userRoles.includes('ROLE_OWNER')) {
    //   window.location.href = ownerDashboardUrl;
    // } else if (userRoles.includes('ROLE_TENANT')) {
    //   window.location.href = tenantMainUrl;
    // } else {
    //   window.location.href = '/main/home';
    // }
    window.location.href = loginSuccessUrl;
    return; // 로그인 페이지에 머무르지 않고 종료
  }

  const loginForm = document.getElementById('loginForm');
  const userTypeButtons = document.querySelectorAll('.user-type-btn');
  let selectedUserType = 'tenant';

  userTypeButtons.forEach(button => {
    button.addEventListener('click', (event) => {
      event.preventDefault();
      userTypeButtons.forEach(btn => btn.classList.remove('active'));
      button.classList.add('active');
      selectedUserType = button.getAttribute('data-user-type');
      console.log("선택된 사용자 타입:", selectedUserType);
    });
  });

  if (loginForm) {
    loginForm.addEventListener('submit', async (event) => {
      event.preventDefault();

      const email = document.getElementById('email').value;
      const password = document.getElementById('password').value;

      const loginData = {
        email: email,
        password: password
      };

      console.log("로그인 데이터:", loginData);

      try {
        const response = await fetch("/api/member/login", {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(loginData)
        });

        if (response.ok) {
          const data = await response.json();
          console.log("로그인 성공! 응답 데이터:", data);

          localStorage.setItem('email', data.email);
          localStorage.setItem('name', data.name);
          localStorage.setItem('jwtToken', `Bearer ${data.token}`);
          localStorage.setItem('userRoles', JSON.stringify(data.roles));

          // 로그인 성공 후 페이지를 리다이렉션
          // if (data.roles.includes('ROLE_OWNER')) {
          //   window.location.href = ownerDashboardUrl;
          // } else if (data.roles.includes('ROLE_TENANT')) {
          //   window.location.href = tenantMainUrl;
          // } else {
          //   window.location.href = '/main/home';
          // }
          window.location.href = loginSuccessUrl;
        } else {
          const errorData = await response.json();
          console.error("로그인 실패:", errorData.message);
          alert("로그인 실패: " + errorData.message);
        }
      } catch (error) {
        console.error("로그인 중 오류가 발생했습니다:", error);
        alert("로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
      }
    });
  }
});

// User Type Selection
function selectUserType(btn, type) {
  // Remove active class from all buttons
  document.querySelectorAll('.user-type-btn').forEach(b => {
    b.classList.remove('active');
  });

  // Add active class to clicked button
  btn.classList.add('active');

  // Update form based on user type
  const loginTitle = document.querySelector('.login-title');
  const loginSubtitle = document.querySelector('.login-subtitle');

  if (type === 'tenant') {
    loginTitle.textContent = '환영합니다!';
    loginSubtitle.textContent = 'SEMO와 함께 안전한 창고 관리를 시작하세요';
    updateInfoSection('tenant');
  } else {
    loginTitle.textContent = '임대인 로그인';
    loginSubtitle.textContent = '창고를 효율적으로 관리하고 수익을 창출하세요';
    updateInfoSection('landlord');
  }
}

// Update right side info based on user type
function updateInfoSection(type) {
  const infoTitle = document.querySelector('.info-title');
  const infoDescription = document.querySelector('.info-description');
  const features = document.querySelector('.info-features');

  if (type === 'landlord') {
    infoTitle.textContent = '스마트한 창고 운영의 파트너';
    infoDescription.innerHTML = 'SEMO와 함께라면 창고 운영이 쉬워집니다<br>효율적인 관리 시스템으로 수익을 극대화하세요';

    features.innerHTML = `
                    <div class="feature">
                        <div class="feature-icon">✓</div>
                        <span>실시간 임대 현황 대시보드</span>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">✓</div>
                        <span>자동 계약 및 결제 관리</span>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">✓</div>
                        <span>임차인 관리 및 소통 도구</span>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">✓</div>
                        <span>수익 분석 및 리포트 제공</span>
                    </div>
                `;
  } else {
    infoTitle.textContent = '안전한 창고 관리의 시작';
    infoDescription.innerHTML = 'SEMO는 최첨단 보안 시스템과 스마트한 관리 도구로<br>여러분의 소중한 물품을 안전하게 보호합니다';

    features.innerHTML = `
                    <div class="feature">
                        <div class="feature-icon">✓</div>
                        <span>24시간 실시간 CCTV 모니터링</span>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">✓</div>
                        <span>스마트폰으로 언제든 출입 관리</span>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">✓</div>
                        <span>온습도 자동 조절 시스템</span>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">✓</div>
                        <span>화재 및 도난 종합 보험 가입</span>
                    </div>
                `;
  }
}

// Form submission
// document.getElementById('loginForm').addEventListener('submit', function(e) {
//   e.preventDefault();
//
//   // Get user type
//   const userType = document.querySelector('.user-type-btn.active').textContent.includes('임차인') ? 'tenant' : 'landlord';
//
//   // Here you would normally send the login request
//   console.log('Logging in as:', userType);
//   alert(`${userType === 'tenant' ? '임차인' : '임대인'}으로 로그인 중...`);
// });

// Social login handlers
document.querySelectorAll('.social-btn').forEach(btn => {
  btn.addEventListener('click', function() {
    const platform = this.textContent.trim();
    const userType = document.querySelector('.user-type-btn.active').textContent.includes('임차인') ? '임차인' : '임대인';
    console.log(`${userType} - ${platform} 로그인`);
    alert(`${platform}으로 ${userType} 로그인을 진행합니다...`);
  });
});