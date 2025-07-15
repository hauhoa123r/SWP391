// =============================================
// MODAL TEMPLATES
// =============================================

const modalTemplates = {
    vital_signs: `
    <div class="modal-header bg-primary text-white">
      <h5 class="modal-title">Dấu hiệu sinh tồn</h5>
      <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
    </div>
    <div class="modal-body">
      <form id="vitalSignsForm">
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">Nhiệt độ (°C)</label>
            <input type="number" step="0.1" class="form-control" id="temperature" min="35" max="42">
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">Huyết áp (mmHg)</label>
            <div class="input-group">
              <input type="number" class="form-control" id="bpSystolic" placeholder="Tâm thu">
              <span class="input-group-text">/</span>
              <input type="number" class="form-control" id="bpDiastolic" placeholder="Tâm trương">
            </div>
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">Nhịp tim (lần/phút)</label>
            <input type="number" class="form-control" id="pulseRate">
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">SpO2 (%)</label>
            <input type="number" class="form-control" id="spo2" min="70" max="100">
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">Nhịp thở (lần/phút)</label>
            <input type="number" class="form-control" id="respiratoryRate">
          </div>
        </div>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
      <button type="button" class="btn btn-primary" onclick="saveExam('vital_signs')">Lưu kết quả</button>
    </div>
  `,

    respiratory: `
    <div class="modal-header bg-primary text-white">
      <h5 class="modal-title">Khám hô hấp</h5>
      <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
    </div>
    <div class="modal-body">
      <form id="respiratoryExamForm">
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">Kiểu thở</label>
            <select class="form-select" id="breathingPattern">
              <option value="normal">Bình thường</option>
              <option value="tachypnea">Thở nhanh</option>
              <option value="bradypnea">Thở chậm</option>
            </select>
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">Rung thanh</label>
            <select class="form-select" id="fremitus">
              <option value="normal">Bình thường</option>
              <option value="increased">Tăng</option>
              <option value="decreased">Giảm</option>
            </select>
          </div>
          <div class="col-12 mb-3">
            <label class="form-label">Khám nghe phổi</label>
            <textarea class="form-control" id="auscultation" rows="3"></textarea>
          </div>
        </div>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
      <button type="button" class="btn btn-primary" onclick="saveExam('respiratory')">Lưu kết quả</button>
    </div>
  `,

    cardiac: `
    <div class="modal-header bg-primary text-white">
      <h5 class="modal-title">Khám tim mạch</h5>
      <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
    </div>
    <div class="modal-body">
      <form id="cardiacExamForm">
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">Nhịp tim (lần/phút)</label>
            <input type="number" class="form-control" id="heartRate">
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">Tiếng tim</label>
            <select class="form-select" id="heartSounds">
              <option value="normal">Bình thường</option>
              <option value="muffled">Mờ</option>
              <option value="split">Tách đôi</option>
            </select>
          </div>
          <div class="col-12 mb-3">
            <label class="form-label">Tiếng thổi</label>
            <textarea class="form-control" id="murmur" rows="3"></textarea>
          </div>
        </div>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
      <button type="button" class="btn btn-primary" onclick="saveExam('cardiac')">Lưu kết quả</button>
    </div>
  `,

    clinical_note: `
    <div class="modal-header bg-primary text-white">
      <h5 class="modal-title">Ghi chú lâm sàng</h5>
      <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
    </div>
    <div class="modal-body">
      <form id="clinicalNoteForm">
        <div class="mb-3">
          <label class="form-label">Nội dung ghi chú</label>
          <textarea class="form-control" id="noteText" rows="5" required></textarea>
        </div>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
      <button type="button" class="btn btn-primary" onclick="saveExam('clinical_note')">Lưu ghi chú</button>
    </div>
  `
};

// =============================================
// DATA STORAGE
// =============================================

let examRecords = [];
let currentId = 1;

// =============================================
// HELPER FUNCTIONS
// =============================================

function getCurrentDateTime() {
    const now = new Date();
    return now.toLocaleString('vi-VN');
}

function getExamIcon(examType) {
    const icons = {
        'vital_signs': 'heartbeat',
        'respiratory': 'lungs',
        'cardiac': 'heart',
        'neurologic': 'brain',
        'gastrointestinal': 'stomach',
        'genitourinary': 'procedures',
        'musculoskeletal': 'bone',
        'dermatologic': 'allergies',
        'clinical_note': 'notes-medical'
    };
    return icons[examType] || 'stethoscope';
}

function getExamTitle(examType) {
    const titles = {
        'vital_signs': 'Dấu hiệu sinh tồn',
        'respiratory': 'Khám hô hấp',
        'cardiac': 'Khám tim mạch',
        'neurologic': 'Khám thần kinh',
        'gastrointestinal': 'Khám tiêu hóa',
        'genitourinary': 'Khám tiết niệu',
        'musculoskeletal': 'Khám cơ xương',
        'dermatologic': 'Khám da liễu',
        'clinical_note': 'Ghi chú lâm sàng'
    };
    return titles[examType] || 'Khám lâm sàng';
}

function renderExamResults() {
    const container = document.getElementById('examResults');

    if (examRecords.length === 0) {
        container.innerHTML = '<div class="alert alert-info">Chưa có dữ liệu khám. Vui lòng thêm mới bằng các nút bên trái.</div>';
        return;
    }

    container.innerHTML = '';

    // Nhóm kết quả theo loại khám
    const examsByType = {};
    examRecords.forEach(exam => {
        if (!examsByType[exam.type]) {
            examsByType[exam.type] = [];
        }
        examsByType[exam.type].push(exam);
    });

    // Hiển thị từng nhóm
    for (const [examType, exams] of Object.entries(examsByType)) {
        const sectionHtml = `
      <div class="mb-4">
        <h5><i class="fas fa-${getExamIcon(examType)} me-2"></i>${getExamTitle(examType)}</h5>
        ${exams.map(exam => renderExamCard(exam)).join('')}
      </div>
    `;
        container.insertAdjacentHTML('beforeend', sectionHtml);
    }
}

function renderExamCard(exam) {
    const details = getExamDetails(exam);
    return `
    <div class="card exam-card mb-3" id="exam-${exam.id}">
      <div class="exam-card-header d-flex justify-content-between align-items-center">
        <div>
          <strong>${details.title}</strong>
          <small class="text-muted ms-2">${exam.recorded_at}</small>
        </div>
        <div>
          <button class="btn btn-sm btn-outline-primary me-1" onclick="editExam(${exam.id})">
            <i class="fas fa-edit"></i> Sửa
          </button>
          <button class="btn btn-sm btn-outline-danger" onclick="deleteExam(${exam.id})">
            <i class="fas fa-trash"></i> Xóa
          </button>
        </div>
      </div>
      <div class="card-body">
        ${details.content}
      </div>
    </div>
  `;
}

function getExamDetails(exam) {
    let title = getExamTitle(exam.type);
    let content = '';

    switch (exam.type) {
        case 'vital_signs':
            content = `
        <div class="row">
          <div class="col-md-4">
            <p><strong>Nhiệt độ:</strong> ${exam.data.temperature || '--'} °C</p>
          </div>
          <div class="col-md-4">
            <p><strong>Huyết áp:</strong> ${exam.data.bpSystolic || '--'}/${exam.data.bpDiastolic || '--'} mmHg</p>
          </div>
          <div class="col-md-4">
            <p><strong>Nhịp tim:</strong> ${exam.data.pulseRate || '--'} lần/phút</p>
          </div>
          <div class="col-md-4">
            <p><strong>SpO2:</strong> ${exam.data.spo2 || '--'} %</p>
          </div>
          <div class="col-md-4">
            <p><strong>Nhịp thở:</strong> ${exam.data.respiratoryRate || '--'} lần/phút</p>
          </div>
        </div>
      `;
            break;

        case 'respiratory':
            content = `
        <p><strong>Kiểu thở:</strong> ${exam.data.breathingPattern || '--'}</p>
        <p><strong>Rung thanh:</strong> ${exam.data.fremitus || '--'}</p>
        <p><strong>Khám nghe phổi:</strong> ${exam.data.auscultation || 'Không có ghi chú'}</p>
      `;
            break;

        case 'cardiac':
            content = `
        <p><strong>Nhịp tim:</strong> ${exam.data.heartRate || '--'} lần/phút</p>
        <p><strong>Tiếng tim:</strong> ${exam.data.heartSounds || '--'}</p>
        <p><strong>Tiếng thổi:</strong> ${exam.data.murmur || 'Không có'}</p>
      `;
            break;

        case 'clinical_note':
            content = `<p>${exam.data.noteText || 'Không có nội dung'}</p>`;
            break;

        default:
            content = '<p>Thông tin khám chi tiết</p>';
    }

    return { title, content };
}

// =============================================
// MODAL FUNCTIONS
// =============================================

function openModal(examType, examId = null) {
    // Tạo modal container nếu chưa có
    let modalContainer = document.getElementById('modalContainer');
    if (!modalContainer) {
        modalContainer = document.createElement('div');
        modalContainer.id = 'modalContainer';
        document.body.appendChild(modalContainer);
    }

    // Tạo modal HTML
    const modalId = `${examType}Modal`;
    modalContainer.innerHTML = `
    <div class="modal fade" id="${modalId}" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          ${modalTemplates[examType] || modalTemplates['clinical_note']}
        </div>
      </div>
    </div>
  `;

    // Hiển thị modal
    const modal = new bootstrap.Modal(document.getElementById(modalId));
    modal.show();

    // Nếu là chỉnh sửa, điền dữ liệu vào form
    if (examId) {
        const exam = examRecords.find(e => e.id === examId);
        if (exam) {
            if (exam.type === 'clinical_note') {
                document.getElementById('noteText').value = exam.data.noteText || '';
            } else {
                for (const [key, value] of Object.entries(exam.data)) {
                    if (document.getElementById(key)) {
                        document.getElementById(key).value = value || '';
                    }
                }
            }
        }
    }
}

// =============================================
// CRUD FUNCTIONS
// =============================================

function saveExam(examType) {
    // Thu thập dữ liệu từ form
    const formData = {};
    let isValid = true;

    switch (examType) {
        case 'vital_signs':
            formData.temperature = document.getElementById('temperature').value;
            formData.bpSystolic = document.getElementById('bpSystolic').value;
            formData.bpDiastolic = document.getElementById('bpDiastolic').value;
            formData.pulseRate = document.getElementById('pulseRate').value;
            formData.spo2 = document.getElementById('spo2').value;
            formData.respiratoryRate = document.getElementById('respiratoryRate').value;
            break;

        case 'respiratory':
            formData.breathingPattern = document.getElementById('breathingPattern').value;
            formData.fremitus = document.getElementById('fremitus').value;
            formData.auscultation = document.getElementById('auscultation').value;
            break;

        case 'cardiac':
            formData.heartRate = document.getElementById('heartRate').value;
            formData.heartSounds = document.getElementById('heartSounds').value;
            formData.murmur = document.getElementById('murmur').value;
            break;

        case 'clinical_note':
            formData.noteText = document.getElementById('noteText').value;
            if (!formData.noteText.trim()) {
                alert('Vui lòng nhập nội dung ghi chú');
                isValid = false;
            }
            break;
    }

    if (!isValid) return;

    // Tạo hoặc cập nhật bản ghi
    const examId = examRecords.find(e => e.type === examType)?.id;
    const examRecord = {
        id: examId || currentId++,
        type: examType,
        data: formData,
        recorded_at: getCurrentDateTime()
    };

    if (examId) {
        // Cập nhật bản ghi hiện có
        const index = examRecords.findIndex(e => e.id === examId);
        examRecords[index] = examRecord;
    } else {
        // Thêm bản ghi mới
        examRecords.push(examRecord);
    }

    // Đóng modal và render lại kết quả
    bootstrap.Modal.getInstance(document.querySelector('.modal')).hide();
    renderExamResults();
}

function editExam(examId) {
    const exam = examRecords.find(e => e.id === examId);
    if (exam) {
        openModal(exam.type, examId);
    }
}

function deleteExam(examId) {
    if (confirm('Bạn có chắc chắn muốn xóa kết quả khám này?')) {
        examRecords = examRecords.filter(e => e.id !== examId);
        renderExamResults();
    }
}

// =============================================
// INITIALIZATION
// =============================================

document.addEventListener('DOMContentLoaded', function() {
    // Hiển thị ngày giờ hiện tại
    document.getElementById('examDate').textContent = getCurrentDateTime();

    // Thêm một số dữ liệu mẫu để demo
    examRecords = [
        {
            id: currentId++,
            type: 'vital_signs',
            data: {
                temperature: 36.5,
                bpSystolic: 120,
                bpDiastolic: 80,
                pulseRate: 72,
                spo2: 98,
                respiratoryRate: 16
            },
            recorded_at: getCurrentDateTime()
        },
        {
            id: currentId++,
            type: 'respiratory',
            data: {
                breathingPattern: 'normal',
                fremitus: 'normal',
                auscultation: 'Âm phổi rõ, không ran'
            },
            recorded_at: getCurrentDateTime()
        },
        {
            id: currentId++,
            type: 'clinical_note',
            data: {
                noteText: 'Bệnh nhân tỉnh táo, tiếp xúc tốt, không khó thở'
            },
            recorded_at: getCurrentDateTime()
        }
    ];

    renderExamResults();
});