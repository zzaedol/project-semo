// 서비스 소개 페이지 JavaScript

// 스무스 스크롤 애니메이션 및 카운팅 애니메이션
document.addEventListener('DOMContentLoaded', function() {
  // 숫자 카운팅 애니메이션
  const statNumbers = document.querySelectorAll('.stat-number');
  
  function animateCount(element, target) {
    let current = 0;
    const increment = target / 50;
    const timer = setInterval(() => {
      current += increment;
      if (current >= target) {
        current = target;
        clearInterval(timer);
      }
      
      if (target === 24) {
        element.textContent = Math.floor(current);
      } else if (target === 100) {
        element.textContent = Math.floor(current) + '+';
      } else if (target === 99) {
        element.textContent = Math.floor(current) + '%';
      }
    }, 20);
  }

  // Intersection Observer로 화면에 보일 때 애니메이션 시작
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const text = entry.target.textContent;
        if (text.includes('24')) {
          animateCount(entry.target, 24);
        } else if (text.includes('1000+')) {
          animateCount(entry.target, 1000);
        } else if (text.includes('99%')) {
          animateCount(entry.target, 99);
        }
      }
    });
  });

  statNumbers.forEach(number => observer.observe(number));

  // 카드 호버 애니메이션
  const featureCards = document.querySelectorAll('.feature-card');
  featureCards.forEach(card => {
    card.addEventListener('mouseenter', function() {
      this.style.transform = 'translateY(-10px) scale(1.02)';
    });
    
    card.addEventListener('mouseleave', function() {
      this.style.transform = 'translateY(0) scale(1)';
    });
  });
});
