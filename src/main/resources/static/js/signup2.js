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
  
  // event 객체가 전역에서 접근 가능하지 않을 수 있으므로 수정
  const clickedCard = event?.currentTarget;
  if (clickedCard) {
    clickedCard.classList.add('selected');
  }

  // Enable next button
  const nextBtn = document.getElementById('nextBtn1');
  if (nextBtn) {
    nextBtn.disabled = false;
  }

  // Update step 3 content based on user type
  const tenantInfo = document.getElementById('tenantInfo');
  const landlordInfo = document.getElementById('landlordInfo');
  const step3Title = document.getElementById('step3Title');
  const step3Subtitle = document.getElementById('step3Subtitle');

  if (type === 'tenant') {
    if (tenantInfo) tenantInfo.style.display = 'block';
    if (landlordInfo) landlordInfo.style.display = 'none';
    if (step3Title) step3Title.textContent = '추가 정보를 입력해주세요';
    if (step3Subtitle) step3Subtitle.textContent = '더 나은 서비스 제공을 위한 선택 정보입니다';
    
    // 임대인 필수 필드 제거
    removeLandlordRequiredFields();
  } else if (type === 'owner') {
    if (tenantInfo) tenantInfo.style.display = 'none';
    if (landlordInfo) landlordInfo.style.display = 'block';
    if (step3Title) step3Title.textContent = '사업자 정보를 입력해주세요';
    if (step3Subtitle) step3Subtitle.textContent = '창고 임대 서비스를 위한 필수 정보입니다';
    
    // 임차인 필수 필드 제거
    removeTenantRequiredFields();
  }
}

// 임대인 필수 필드 제거 (임차인 선택 시)
function removeLandlordRequiredFields() {
  const landlordFields = [
    'businessRegistrationNumber',
    'companyName',
    'ceoName'
  ];

  landlordFields.forEach(fieldId => {
    const field = document.getElementById(fieldId);
    if (field) {
      field.removeAttribute('required');
    }
  });
}

// 임차인 필수 필드 제거 (임대인 선택 시)
function removeTenantRequiredFields() {
  const tenantFields = [
    'address',
    'detailedAddress',
    'purpose',
    'requiredSize'
  ];
  
  tenantFields.forEach(fieldId => {
    const field = document.getElementById(fieldId);
    if (field) {
      field.removeAttribute('required');
    }
  });
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
function nextStep(event) {
  if (event) {
    event.preventDefault();
  }
  
  if (currentStep < totalSteps) {
    // Validation for current step
    if (currentStep === 1 && !selectedUserType) {
      alert('회원 유형을 선택해주세요.');
      return;
    }

    if (currentStep === 2 && !validateStep2()) {
      return;
    }

    if (currentStep === 3 && !validateStep3()) {
      return;
    }

    // Hide current step
    const currentStepElement = document.getElementById(`step${currentStep}`);
    if (currentStepElement) {
      currentStepElement.classList.remove('active');
    }

    // Show next step
    currentStep++;
    const nextStepElement = document.getElementById(`step${currentStep}`);
    if (nextStepElement) {
      nextStepElement.classList.add('active');
    }

    // Update progress bar
    updateProgressBar();

    // Scroll to top
    window.scrollTo({top: 0, behavior: 'smooth'});
  }
}

// Step 2 validation (기본 정보)
function validateStep2() {
  const name = document.getElementById('name');
  const phoneNumber = document.getElementById('phoneNumber');
  const email = document.getElementById('email');
  const password = document.getElementById('password');
  const passwordConfirm = document.getElementById('passwordConfirm');

  name.nextElementSibling.innerHTML = '';
  phoneNumber.nextElementSibling.innerHTML = '';
  email.nextElementSibling.innerHTML = '';
  password.nextElementSibling.innerHTML = '';
  passwordConfirm.nextElementSibling.innerHTML = '';

  let isValid = true;
  const errors = [];

  // 이름 검증
  if (!name?.value.trim()) {
    name?.classList.add('error');
    errors.push('이름을 입력해주세요.');
    name.nextElementSibling.innerHTML = '이름을 입력해주세요.';
    name?.nextElementSibling.classList.add('show');
    isValid = false;
  } else {
    name?.classList.remove('error');
  }

  // 전화번호 검증
  const phoneRegex = /^010-\d{4}-\d{4}$/;
  if (!phoneNumber?.value.trim()) {
    phoneNumber?.classList.add('error');
    errors.push('휴대폰 번호를 입력해주세요.');
    phoneNumber.nextElementSibling.innerHTML = '휴대폰 번호를 입력해주세요.';
    phoneNumber?.nextElementSibling.classList.add('show');
    isValid = false;
  } else if (!phoneRegex.test(phoneNumber.value)) {
    phoneNumber?.classList.add('error');
    errors.push('휴대폰 번호 형식이 올바르지 않습니다. (010-0000-0000)');
    phoneNumber.nextElementSibling.innerHTML = '휴대폰 번호 형식이 올바르지 않습니다. (010-0000-0000)';
    phoneNumber?.nextElementSibling.classList.add('show');
    isValid = false;
  } else {
    phoneNumber?.classList.remove('error');
  }

  // 이메일 검증
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!email?.value.trim()) {
    email?.classList.add('error');
    errors.push('이메일을 입력해주세요.');
    email.nextElementSibling.innerHTML = '이메일을 입력해주세요.';
    email?.nextElementSibling.classList.add('show');
    isValid = false;
  } else if (!emailRegex.test(email.value)) {
    email?.classList.add('error');
    errors.push('이메일 형식이 올바르지 않습니다.');
    email.nextElementSibling.innerHTML = '이메일 형식이 올바르지 않습니다.';
    email?.nextElementSibling.classList.add('show');
    isValid = false;
  } else {
    email?.classList.remove('error');
  }

  // 비밀번호 검증
  //const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{}':"\\|,.<>\/?~])[A-Za-z\d!@#$%^&*()_+\-=\[\]{}':"\\|,.<>\/?~]{8,}$/;
  if (!password?.value) {
    password?.classList.add('error');
    errors.push('비밀번호를 입력해주세요.');
    password.nextElementSibling.innerHTML = '비밀번호를 입력해주세요.';
    password?.nextElementSibling.classList.add('show');
    isValid = false;
  } else if (!passwordRegex.test(password.value)) {
    console.log(passwordRegex.test(password.value));
    password?.classList.add('error');
    errors.push('비밀번호는 8자 이상, 영문+숫자+특수문자를 포함해야 합니다.');
    password.nextElementSibling.innerHTML = '비밀번호는 8자 이상, 영문+숫자+특수문자를 포함해야 합니다.';
    password?.nextElementSibling.classList.add('show');
    isValid = false;
  } else {
    password?.classList.remove('error');
  }

  // 비밀번호 확인 검증
  if (!passwordConfirm?.value) {
    passwordConfirm?.classList.add('error');
    errors.push('비밀번호 확인을 입력해주세요.');
    passwordConfirm.nextElementSibling.innerHTML = '비밀번호 확인을 입력해주세요.';
    passwordConfirm?.nextElementSibling.classList.add('show');
    isValid = false;
  } else if (password?.value !== passwordConfirm.value) {
    passwordConfirm?.classList.add('error');
    errors.push('비밀번호가 일치하지 않습니다.');
    passwordConfirm.nextElementSibling.innerHTML = '비밀번호가 일치하지 않습니다.';
    passwordConfirm?.nextElementSibling.classList.add('show');
    isValid = false;
  } else {
    passwordConfirm?.classList.remove('error');
  }

  if (!isValid) {

  }

  return isValid;
}

// Step 3 validation (추가 정보)
function validateStep3() {
  if (selectedUserType === 'landlord') {
    return validateLandlordInfo();
  }
  // 임차인은 선택 사항이므로 항상 true 반환
  return true;
}

// 임대인 정보 검증
function validateLandlordInfo() {
  const businessNumber = document.getElementById('businessRegistrationNumber');
  const companyName = document.getElementById('companyName');
  const ceoName = document.getElementById('ceoName');

  let isValid = true;
  const errors = [];

  // 사업자등록번호 검증
  const businessNumberRegex = /^\d{3}-\d{2}-\d{5}$/;
  if (!businessNumber?.value.trim()) {
    businessNumber?.classList.add('error');
    errors.push('사업자등록번호를 입력해주세요.');
    isValid = false;
  } else if (!businessNumberRegex.test(businessNumber.value)) {
    businessNumber?.classList.add('error');
    errors.push('사업자등록번호 형식이 올바르지 않습니다. (000-00-00000)');
    isValid = false;
  } else {
    businessNumber?.classList.remove('error');
  }

  // 회사명 검증
  if (!companyName?.value.trim()) {
    companyName?.classList.add('error');
    errors.push('회사명을 입력해주세요.');
    isValid = false;
  } else {
    companyName?.classList.remove('error');
  }

  // 대표자명 검증
  if (!ceoName?.value.trim()) {
    ceoName?.classList.add('error');
    errors.push('대표자명을 입력해주세요.');
    isValid = false;
  } else {
    ceoName?.classList.remove('error');
  }

  if (!isValid) {
    alert(errors[0]);
  }

  return isValid;
}

function prevStep() {
  if (currentStep > 1) {
    // Hide current step
    const currentStepElement = document.getElementById(`step${currentStep}`);
    if (currentStepElement) {
      currentStepElement.classList.remove('active');
    }

    // Show previous step
    currentStep--;
    const prevStepElement = document.getElementById(`step${currentStep}`);
    if (prevStepElement) {
      prevStepElement.classList.add('active');
    }

    // Update progress bar
    updateProgressBar();

    // Scroll to top
    window.scrollTo({top: 0, behavior: 'smooth'});
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
async function submitForm() {
  try {
    // Form data 수집
    const formData = collectFormData();
    
    console.log('Form submitted with data:', formData);

    // TODO: 서버로 데이터 전송
    const response = await fetch(signupUrl, {
      method: 'POST',
      body: formData,
    });

    if (response.ok || response.status === 201) {
      // **성공:** 서버에서 200 또는 201 응답을 받으면 Step 5로 전환 (프론트엔드 로직)
      // 성공 메시지 등을 처리할 수 있습니다.
      // Move to success step
      const currentStepElement = document.getElementById(`step${currentStep}`);
      if (currentStepElement) {
        currentStepElement.classList.remove('active');
      }

      currentStep = 5;
      const successStepElement = document.getElementById(`step${currentStep}`);
      if (successStepElement) {
        successStepElement.classList.add('active');
      }

      updateProgressBar();

      // document.getElementById('step4').style.display = 'none';
      // document.getElementById('step5').style.display = 'block';
      // updateProgress(5); // Step 5로 UI 업데이트
    } else {
      // **실패:** 서버에서 4xx 또는 5xx 응답을 받은 경우
      const errorMessage = await response.text(); // 서버에서 보낸 에러 메시지 텍스트를 받음
      alert('회원가입 실패: ' + errorMessage);
    }
  } catch (error) {
    console.error('Signup error:', error);
    //alert('회원가입 중 오류가 발생했습니다. 다시 시도해주세요.');
  }
}

// // Form data 수집
// function collectFormData() {
//   const formData = {
//     userType: selectedUserType,
//     memberDTO: {
//       name: document.getElementById('name')?.value || '',
//       phoneNumber: document.getElementById('phoneNumber')?.value || '',
//       email: document.getElementById('email')?.value || '',
//       password: document.getElementById('password')?.value || ''
//     },
//     termsDTO: {
//       agreeAll: document.getElementById('agreeAll')?.checked || false,
//       terms1: document.getElementById('terms1')?.checked || false,
//       terms2: document.getElementById('terms2')?.checked || false,
//       terms3: document.getElementById('terms3')?.checked || false,
//       terms4: document.getElementById('terms4')?.checked || false,
//       terms5: document.getElementById('terms5')?.checked || false
//     }
//   };
//
//   if (selectedUserType === 'tenant') {
//     formData.tenantProfileDTO = {
//       address: document.getElementById('address')?.value || '',
//       detailedAddress: document.getElementById('detailedAddress')?.value || '',
//       purpose: document.getElementById('purpose')?.value || '',
//       requiredSizeCode: document.getElementById('requiredSize')?.value || ''
//     };
//   } else if (selectedUserType === 'landlord') {
//     formData.ownerProfileDTO = {
//       businessRegistrationNumber: document.getElementById('businessRegistrationNumber')?.value || '',
//       companyName: document.getElementById('companyName')?.value || '',
//       ceoName: document.getElementById('ceoName')?.value || '',
//       officePhoneNumber: document.getElementById('officePhoneNumber')?.value || '',
//       businessFile: document.getElementById('businessFile').files[0] || ''
//     };
//
//     // 창고 정보 (선택)
//     const warehouseAddress = document.getElementById('warehouseAddress')?.value;
//     const warehouseAreaSqm = document.getElementById('warehouseSize')?.value;
//
//     if (warehouseAddress || warehouseAreaSqm) {
//       formData.warehouseDTO = {
//         address: warehouseAddress || '',
//         areaSqm: warehouseAreaSqm || ''
//       };
//     }
//   }
//
//   return formData;
// }

/**
 * 중첩된 JavaScript 객체를 Spring Boot @ModelAttribute가 인식할 수 있는
 * 플랫한 FormData 객체로 변환하여 반환합니다.
 *
 * @returns {FormData} Spring Boot 전송용 FormData 객체
 */
function collectFormData() {
  // 1. 기존 로직: 중첩된 객체 형태로 데이터를 수집합니다.
  const data = {
    userType: selectedUserType,
    memberDTO: {
      name: document.getElementById('name')?.value || '',
      phoneNumber: document.getElementById('phoneNumber')?.value || '',
      email: document.getElementById('email')?.value || '',
      password: document.getElementById('password')?.value || ''
    },
    termsDTO: {
      agreeAll: document.getElementById('agreeAll')?.checked || false,
      terms1: document.getElementById('terms1')?.checked || false,
      terms2: document.getElementById('terms2')?.checked || false,
      terms3: document.getElementById('terms3')?.checked || false,
      terms4: document.getElementById('terms4')?.checked || false,
      terms5: document.getElementById('terms5')?.checked || false
    }
  };

  // 2. userType에 따른 추가 DTO 정보 수집
  if (selectedUserType === 'tenant') {
    data.tenantProfileDTO = {
      address: document.getElementById('address')?.value || '',
      detailedAddress: document.getElementById('detailedAddress')?.value || '',
      purpose: document.getElementById('purpose')?.value || '',
      requiredSizeCode: document.getElementById('requiredSize')?.value || ''
    };
  } else if (selectedUserType === 'owner') {
    // ownerProfileDTO 수집
    data.ownerProfileDTO = {
      businessRegistrationNumber: document.getElementById('businessRegistrationNumber')?.value || '',
      companyName: document.getElementById('companyName')?.value || '',
      ceoName: document.getElementById('ceoName')?.value || '',
      officePhoneNumber: document.getElementById('officePhoneNumber')?.value || '',
      // File 객체를 그대로 담습니다.
      businessFile: document.getElementById('businessFile')?.files[0] || null
    };

    // warehouseDTO 수집 (선택적)
    const warehouseAddress = document.getElementById('warehouseAddress')?.value;
    const warehouseAreaSqm = document.getElementById('warehouseSize')?.value;

    if (warehouseAddress || warehouseAreaSqm) {
      data.warehouseDTO = {
        address: warehouseAddress || '',
        areaSqm: warehouseAreaSqm || ''
      };
    }
  }

  // 3. 중첩 객체를 플랫한 FormData로 변환하여 반환
  const formData = new FormData();
  buildFormData(formData, data); // FormData 변환 함수 호출

  return formData;
}

/**
 * 재귀적으로 객체를 순회하며 FormData에 'parent.child' 형태의 키로 추가하는 함수입니다.
 * File 객체는 그대로 추가합니다.
 * * @param {FormData} formData - 데이터를 추가할 FormData 객체
 * @param {Object} data - 변환할 JavaScript 객체
 * @param {string} [parentKey] - 현재 객체의 부모 키 (재귀 호출용)
 */
function buildFormData(formData, data, parentKey) {
  if (
      data &&
      typeof data === 'object' &&
      !(data instanceof Date) &&
      !(data instanceof File) &&
      !(data instanceof Blob)
  ) {
    // 객체이면서 File/Date/Blob이 아닌 경우 재귀적으로 순회
    Object.keys(data).forEach(key => {
      buildFormData(formData, data[key], parentKey ? `${parentKey}.${key}` : key);
    });
  } else if (data !== undefined && data !== null) {
    // 최종 값에 도달했거나, File 객체인 경우 FormData에 추가

    // 빈 문자열인 경우 (값이 없는 경우), 전송하지 않거나 null/빈 문자열로 명시적으로 추가
    // Spring 바인딩을 위해 빈 문자열도 명시적으로 추가하는 것이 안전합니다.
    const value = data === '' ? '' : data;

    // parentKey가 없는 경우는 최상위 필드 (userType)
    const key = parentKey || '';

    // 키와 값을 FormData에 추가 (File 객체도 여기서 추가됩니다.)
    // File 객체의 경우, FormData가 Content-Type을 자동으로 처리합니다.
    formData.append(key, value);
  }
}

// File upload handling
const businessFileInput = document.getElementById('businessFile');
if (businessFileInput) {
  businessFileInput.addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
      // 파일 크기 검증 (5MB)
      const maxSize = 5 * 1024 * 1024;
      if (file.size > maxSize) {
        alert('파일 크기는 5MB 이하여야 합니다.');
        this.value = '';
        return;
      }

      // 파일 형식 검증
      const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf'];
      if (!allowedTypes.includes(file.type)) {
        alert('JPG, PNG, PDF 형식의 파일만 업로드 가능합니다.');
        this.value = '';
        return;
      }

      const label = this.parentElement.querySelector('.file-upload-label');
      if (label) {
        label.innerHTML = `📄 ${file.name}`;
        label.style.color = '#4CAF50';
      }
    }
  });
}

// Input validation
document.querySelectorAll('.form-input').forEach(input => {
  input.addEventListener('blur', function() {
    validateInput(this);
  });

  // 실시간 입력 시 에러 제거
  input.addEventListener('input', function() {
    if (this.classList.contains('error')) {
      this.classList.remove('error');
      const errorMsg = this.nextElementSibling;
      if (errorMsg && errorMsg.classList.contains('error-message')) {
        errorMsg.classList.remove('show');
      }
    }
  });
});

// 개별 input 검증 함수
function validateInput(input) {
  const errorMsg = input.nextElementSibling;
  
  // 필수 필드 검증
  if (input.hasAttribute('required') && !input.value.trim()) {
    input.classList.add('error');
    if (errorMsg && errorMsg.classList.contains('error-message')) {
      errorMsg.classList.add('show');
    }
    return false;
  }

  // Email validation
  if (input.type === 'email' && input.value) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(input.value)) {
      input.classList.add('error');
      if (errorMsg && errorMsg.classList.contains('error-message')) {
        errorMsg.classList.add('show');
      }
      return false;
    } else {
      input.classList.remove('error');
      if (errorMsg && errorMsg.classList.contains('error-message')) {
        errorMsg.classList.remove('show');
      }
    }
  }

  // Password validation
  if (input.id === 'password' && input.value) {
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    if (!passwordRegex.test(input.value)) {
      input.classList.add('error');
      if (errorMsg && errorMsg.classList.contains('error-message')) {
        errorMsg.classList.add('show');
      }
      return false;
    } else {
      input.classList.remove('error');
      if (errorMsg && errorMsg.classList.contains('error-message')) {
        errorMsg.classList.remove('show');
      }
    }
  }

  // Password confirmation
  if (input.id === 'passwordConfirm' && input.value) {
    const password = document.getElementById('password');
    if (password && input.value !== password.value) {
      input.classList.add('error');
      if (errorMsg && errorMsg.classList.contains('error-message')) {
        errorMsg.classList.add('show');
      }
      return false;
    } else {
      input.classList.remove('error');
      if (errorMsg && errorMsg.classList.contains('error-message')) {
        errorMsg.classList.remove('show');
      }
    }
  }

  return true;
}

// Phone number formatting (휴대폰 번호)
const memberPhoneInput = document.getElementById('phoneNumber');
if (memberPhoneInput) {
  memberPhoneInput.addEventListener('input', function(e) {
    let value = e.target.value.replace(/[^\d]/g, '');
    if (value.length > 3 && value.length <= 7) {
      value = value.slice(0, 3) + '-' + value.slice(3);
    } else if (value.length > 7) {
      value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7, 11);
    }
    e.target.value = value;
  });
}

// Business registration number formatting (사업자등록번호)
const businessNumberInput = document.getElementById('businessRegistrationNumber');
if (businessNumberInput) {
  businessNumberInput.addEventListener('input', function(e) {
    let value = e.target.value.replace(/[^\d]/g, '');
    if (value.length > 3 && value.length <= 5) {
      value = value.slice(0, 3) + '-' + value.slice(3);
    } else if (value.length > 5) {
      value = value.slice(0, 3) + '-' + value.slice(3, 5) + '-' + value.slice(5, 10);
    }
    e.target.value = value;
  });
}

// Office phone number formatting (사업장 전화번호)
const officePhoneInput = document.getElementById('officePhoneNumber');
if (officePhoneInput) {
  officePhoneInput.addEventListener('input', function(e) {
    let value = e.target.value.replace(/[^\d]/g, '');
    
    // 서울 지역번호 (02)
    if (value.startsWith('02')) {
      if (value.length > 2 && value.length <= 6) {
        value = value.slice(0, 2) + '-' + value.slice(2);
      } else if (value.length > 6) {
        value = value.slice(0, 2) + '-' + value.slice(2, 6) + '-' + value.slice(6, 10);
      }
    }
    // 기타 지역번호 (031, 032, 등)
    else {
      if (value.length > 3 && value.length <= 7) {
        value = value.slice(0, 3) + '-' + value.slice(3);
      } else if (value.length > 7) {
        value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7, 11);
      }
    }
    
    e.target.value = value;
  });
}

// Initialize
updateProgressBar();