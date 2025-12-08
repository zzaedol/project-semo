let currentStep = 1;
let selectedUserType = null;
const totalSteps = 5;

// User type selection
function selectUserType(type) {
  selectedUserType = type;

  // Update UI
  document.querySelectorAll('.user-type-card').forEach(card => {
    card.classList.remove('selected');
  });
  event.currentTarget.classList.add('selected');

  // Enable next button
  document.getElementById('nextBtn1').disabled = false;

  // Update step 3 content based on user type
  if (type === 'tenant') {
    document.getElementById('tenantInfo').style.display = 'block';
    document.getElementById('landlordInfo').style.display = 'none';
    document.getElementById('step3Title').textContent = '추가 정보를 입력해주세요';
    document.getElementById('step3Subtitle').textContent = '더 나은 서비스 제공을 위한 선택 정보입니다';
  } else {
    document.getElementById('tenantInfo').style.display = 'none';
    document.getElementById('landlordInfo').style.display = 'block';
    document.getElementById('step3Title').textContent = '사업자 정보를 입력해주세요';
    document.getElementById('step3Subtitle').textContent = '창고 임대 서비스를 위한 필수 정보입니다';
  }
}

// Progress bar update
function updateProgressBar() {
  const progressLine = document.getElementById('progressLine');
  const progressPercent = ((currentStep - 1) / (totalSteps - 1)) * 100;
  progressLine.style.width = progressPercent + '%';

  // Update step circles
  for (let i = 1; i <= totalSteps; i++) {
    const circle = document.getElementById(`step${i}Circle`);
    const label = circle.parentElement.querySelector('.step-label');

    if (i < currentStep) {
      circle.classList.remove('active');
      circle.classList.add('completed');
      circle.innerHTML = '✓';
      label.classList.remove('active');
    } else if (i === currentStep) {
      circle.classList.add('active');
      circle.classList.remove('completed');
      if (i < 5) circle.innerHTML = i;
      label.classList.add('active');
    } else {
      circle.classList.remove('active', 'completed');
      if (i < 5) circle.innerHTML = i;
      label.classList.remove('active');
    }
  }
}

// Navigation functions
function nextStep() {
  if (currentStep < totalSteps) {
    // Validation for current step
    if (currentStep === 1 && !selectedUserType) {
      alert('회원 유형을 선택해주세요.');
      return;
    }

    if (currentStep === 2) {
      // Basic validation for step 2
      const inputs = document.querySelectorAll('#step2 .form-input[required]');
      let isValid = true;
      inputs.forEach(input => {
        if (!input.value) {
          input.classList.add('error');
          isValid = false;
        } else {
          input.classList.remove('error');
        }
      });

      if (!isValid) {
        alert('필수 정보를 모두 입력해주세요.');
        return;
      }
    }

    // Hide current step
    document.getElementById(`step${currentStep}`).classList.remove('active');

    // Show next step
    currentStep++;
    document.getElementById(`step${currentStep}`).classList.add('active');

    // Update progress bar
    updateProgressBar();

    // Scroll to top
    window.scrollTo(0, 0);
  }
}

function prevStep() {
  if (currentStep > 1) {
    // Hide current step
    document.getElementById(`step${currentStep}`).classList.remove('active');

    // Show previous step
    currentStep--;
    document.getElementById(`step${currentStep}`).classList.add('active');

    // Update progress bar
    updateProgressBar();

    // Scroll to top
    window.scrollTo(0, 0);
  }
}

// Terms agreement
function toggleAllCheckboxes() {
  const allCheckbox = document.getElementById('agreeAll');
  const checkboxes = document.querySelectorAll('.agree-checkbox');

  checkboxes.forEach(checkbox => {
    checkbox.checked = allCheckbox.checked;
  });

  checkSubmitButton();
}

// Check if required terms are agreed
function checkSubmitButton() {
  const required = ['terms1', 'terms2', 'terms3'];
  let allRequiredChecked = true;

  required.forEach(id => {
    if (!document.getElementById(id).checked) {
      allRequiredChecked = false;
    }
  });

  document.getElementById('submitBtn').disabled = !allRequiredChecked;
}

// Add event listeners to checkboxes
document.querySelectorAll('.agree-checkbox').forEach(checkbox => {
  checkbox.addEventListener('change', () => {
    checkSubmitButton();

    // Update "all" checkbox
    const allChecked = Array.from(document.querySelectorAll('.agree-checkbox'))
    .every(cb => cb.checked);
    document.getElementById('agreeAll').checked = allChecked;
  });
});

// Submit form
function submitForm() {
  // Here you would normally submit the form data to the server
  console.log('Form submitted');
  console.log('User type:', selectedUserType);

  // Move to success step
  document.getElementById(`step${currentStep}`).classList.remove('active');
  currentStep = 5;
  document.getElementById(`step${currentStep}`).classList.add('active');
  updateProgressBar();

  // Simulate sending welcome email
  setTimeout(() => {
    console.log('Welcome email sent!');
  }, 1000);
}

// File upload handling
document.getElementById('businessFile')?.addEventListener('change', function(e) {
  const file = e.target.files[0];
  if (file) {
    const label = document.querySelector('.file-upload-label');
    label.innerHTML = `📄 ${file.name}`;
    label.style.color = '#4CAF50';
  }
});

// Input validation
document.querySelectorAll('.form-input').forEach(input => {
  input.addEventListener('blur', function() {
    if (this.hasAttribute('required') && !this.value) {
      this.classList.add('error');
    } else {
      this.classList.remove('error');
    }

    // Email validation
    if (this.type === 'email' && this.value) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.value)) {
        this.classList.add('error');
        this.nextElementSibling?.classList.add('show');
      } else {
        this.classList.remove('error');
        this.nextElementSibling?.classList.remove('show');
      }
    }

    // Password validation
    if (this.type === 'password' && this.placeholder.includes('8자')) {
      const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
      if (!passwordRegex.test(this.value)) {
        this.classList.add('error');
        this.nextElementSibling?.classList.add('show');
      } else {
        this.classList.remove('error');
        this.nextElementSibling?.classList.remove('show');
      }
    }

    // Password confirmation
    if (this.placeholder === '비밀번호를 다시 입력하세요') {
      const password = document.querySelector('input[placeholder*="영문+숫자"]').value;
      if (this.value !== password) {
        this.classList.add('error');
        this.nextElementSibling?.classList.add('show');
      } else {
        this.classList.remove('error');
        this.nextElementSibling?.classList.remove('show');
      }
    }
  });
});

// Phone number formatting
document.querySelector('input[type="tel"]')?.addEventListener('input', function(e) {
  let value = e.target.value.replace(/[^\d]/g, '');
  if (value.length > 3 && value.length <= 7) {
    value = value.slice(0, 3) + '-' + value.slice(3);
  } else if (value.length > 7) {
    value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7, 11);
  }
  e.target.value = value;
});

// Initialize
updateProgressBar();