// 요금안내 페이지 JavaScript

// 결제 주기 토글 기능
function toggleBilling(type) {
  const monthlyBtn = document.querySelector('.toggle-option:first-child');
  const yearlyBtn = document.querySelector('.toggle-option:last-child');
  
  const basicPrice = document.getElementById('basic-price');
  const standardPrice = document.getElementById('standard-price');
  const premiumPrice = document.getElementById('premium-price');
  
  const basicOriginal = document.getElementById('basic-original');
  const standardOriginal = document.getElementById('standard-original');
  const premiumOriginal = document.getElementById('premium-original');

  if (type === 'yearly') {
    monthlyBtn.classList.remove('active');
    yearlyBtn.classList.add('active');
    
    // 년 단위 가격 (10개월 가격)
    basicPrice.textContent = '290,000';
    standardPrice.textContent = '490,000';
    premiumPrice.textContent = '990,000';
    
    // 원래 가격 표시
    basicOriginal.style.display = 'block';
    standardOriginal.style.display = 'block';
    premiumOriginal.style.display = 'block';
    
    basicOriginal.textContent = '348,000원';
    standardOriginal.textContent = '588,000원';
    premiumOriginal.textContent = '1,188,000원';
    
    // 결제 주기 텍스트 변경
    document.querySelectorAll('.pricing-period').forEach(el => {
      el.textContent = '원/년';
    });
    
  } else {
    monthlyBtn.classList.add('active');
    yearlyBtn.classList.remove('active');
    
    // 월 단위 가격
    basicPrice.textContent = '29,000';
    standardPrice.textContent = '49,000';
    premiumPrice.textContent = '99,000';
    
    // 원래 가격 숨김
    basicOriginal.style.display = 'none';
    standardOriginal.style.display = 'none';
    premiumOriginal.style.display = 'none';
    
    // 결제 주기 텍스트 변경
    document.querySelectorAll('.pricing-period').forEach(el => {
      el.textContent = '원/월';
    });
  }
}

// FAQ 토글 기능 및 요금제 카드 선택 기능
document.addEventListener('DOMContentLoaded', function() {
  // FAQ 토글
  const faqItems = document.querySelectorAll('.faq-item');
  
  faqItems.forEach(item => {
    const question = item.querySelector('.faq-question');
    
    question.addEventListener('click', function() {
      const isActive = item.classList.contains('active');
      
      // 다른 FAQ 아이템들을 모두 닫기
      faqItems.forEach(otherItem => {
        otherItem.classList.remove('active');
      });
      
      // 클릭한 아이템만 토글
      if (!isActive) {
        item.classList.add('active');
      }
    });
  });

  // 요금제 카드 선택 기능
  const pricingCards = document.querySelectorAll('.pricing-card');
  let selectedCard = null;

  pricingCards.forEach(card => {
    card.addEventListener('click', function() {
      // 이전에 선택된 카드의 selected 클래스 제거
      if (selectedCard) {
        selectedCard.classList.remove('selected');
      }

      // 현재 카드에 selected 클래스 추가
      this.classList.add('selected');
      selectedCard = this;

      // 선택된 플랜 정보를 콘솔에 출력 (나중에 폼 전송 등에 활용)
      const planName = this.querySelector('.pricing-title').textContent;
      const planPrice = this.querySelector('.pricing-price').textContent;
      console.log(`선택된 플랜: ${planName}, 가격: ${planPrice}원`);

      // 선택된 카드의 버튼 텍스트 변경
      updateButtonTexts(planName);
    });

    // 기존 호버 애니메이션은 선택되지 않은 카드에만 적용
    card.addEventListener('mouseenter', function() {
      if (!this.classList.contains('selected')) {
        this.style.transform = 'translateY(-10px) scale(1.02)';
      }
    });
    
    card.addEventListener('mouseleave', function() {
      if (!this.classList.contains('selected')) {
        this.style.transform = 'translateY(0) scale(1)';
      }
    });
  });
});

// 버튼 텍스트 업데이트 함수
function updateButtonTexts(selectedPlan) {
  const buttons = document.querySelectorAll('.pricing-button');
  
  buttons.forEach((button) => {
    const card = button.closest('.pricing-card');
    const planName = card.querySelector('.pricing-title').textContent;
    
    if (card.classList.contains('selected')) {
      button.textContent = `${planName} 선택됨 ✓`;
      button.style.background = 'linear-gradient(135deg, #4caf50, #388e3c)';
    } else {
      button.textContent = `${planName} 선택하기`;
      button.style.background = 'linear-gradient(135deg, #42a5f5, #2196f3)';
    }
  });
}

// 선택된 플랜 정보 가져오기 함수 (나중에 폼 전송 시 활용)
function getSelectedPlan() {
  const selectedCard = document.querySelector('.pricing-card.selected');
  if (selectedCard) {
    const planName = selectedCard.querySelector('.pricing-title').textContent;
    const planPrice = selectedCard.querySelector('.pricing-price').textContent;
    const billingType = document.querySelector('.toggle-option.active').textContent.includes('월') ? 'monthly' : 'yearly';
    
    return {
      name: planName,
      price: planPrice,
      billing: billingType
    };
  }
  return null;
}
