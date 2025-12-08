// 고객센터 전용 스크립트
document.addEventListener('DOMContentLoaded', () => {
  /* FAQ 아코디언 (한 번에 하나만 열림) */
  const faqItems = document.querySelectorAll('.faq-item');

  faqItems.forEach((item) => {
    const question = item.querySelector('.faq-question');
    const answer   = item.querySelector('.faq-answer');
    if (!question || !answer) return; // 구조 불완전 시 스킵

    // 접근성 속성
    question.setAttribute('role', 'button');
    question.setAttribute('tabindex', '0');
    question.setAttribute('aria-expanded', 'false');
    answer.setAttribute('aria-hidden', 'true');

    const open = () => {
      faqItems.forEach(i => i.classList.remove('active')); // 하나만 열기
      item.classList.add('active');
      question.setAttribute('aria-expanded', 'true');
      answer.setAttribute('aria-hidden', 'false');
    };
    const close = () => {
      item.classList.remove('active');
      question.setAttribute('aria-expanded', 'false');
      answer.setAttribute('aria-hidden', 'true');
    };
    const toggle = () => (item.classList.contains('active') ? close() : open());

    question.addEventListener('click', toggle);
    question.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        toggle();
      }
    });
  });

  /* 빠른 접근 스무스 스크롤 (quick-btn만; 빈 # 무시; 대상 없으면 바인딩 안 함) */
  document.querySelectorAll('.quick-btn[href^="#"]').forEach((a) => {
    const hash = a.hash;                 // "#faq"
    if (!hash || hash === '#') return;   // 빈 앵커 스킵

    const id = hash.slice(1);            // "faq"
    const target = document.getElementById(id);
    if (!target) return;                 // 대상 없으면 스킵

    a.addEventListener('click', (e) => {
      e.preventDefault();
      target.scrollIntoView({ behavior: 'smooth', block: 'start' });
    });
  });

  /* 더 많은 공지사항 보기 버튼 */
  const loadMoreBtn = document.getElementById('loadMoreNotices');
  if (loadMoreBtn) {
    const noticesPerLoad = 5; // 한 번에 보여줄 공지사항 개수
    let currentlyVisible = 5; // 초기 표시 개수

    loadMoreBtn.addEventListener('click', (e) => {
      e.preventDefault();

      const hiddenNotices = document.querySelectorAll('.notice-item.notice-hidden');
      const totalNotices = document.querySelectorAll('.notice-item').length;

      // 숨겨진 공지사항 중 일부를 표시
      for (let i = 0; i < noticesPerLoad && i < hiddenNotices.length; i++) {
        hiddenNotices[i].classList.remove('notice-hidden');
        hiddenNotices[i].classList.add('notice-visible');
      }

      currentlyVisible += noticesPerLoad;

      // 모든 공지사항이 표시되면 버튼 숨기기
      const remainingHidden = document.querySelectorAll('.notice-item.notice-hidden').length;
      if (remainingHidden === 0) {
        loadMoreBtn.style.display = 'none';
      }
    });
  }
});
