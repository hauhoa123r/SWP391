function openExamModal(examType, examData = null) {
    // Safely hide examTypeModal if it exists and is initialized
    const examTypeModalElement = document.getElementById('examTypeModal');
    if (examTypeModalElement) {
        const examTypeModal = bootstrap.Modal.getInstance(examTypeModalElement);
        if (examTypeModal) {
            examTypeModal.hide();
        }
    }

    // Set modal title
    const titles = {
        vital: 'Dấu hiệu sinh tồn',
        respiratory: 'Khám hô hấp',
        cardiac: 'Khám tim mạch',
        neurologic: 'Khám thần kinh',
        gastro: 'Khám tiêu hóa',
        genitourinary: 'Khám tiết niệu',
        musculoskeletal: 'Khám cơ xương',
        dermatologic: 'Khám da liễu',
        notes: 'Ghi chú khác'
    };
    document.getElementById('examTitle').textContent = examData ? `Cập nhật ${titles[examType]}` : titles[examType];

    // Load the form with or without prefilled data
    loadExamForm(examType, examData);

    // Show the exam detail modal
    new bootstrap.Modal(document.getElementById('examDetailModal')).show();
}

// Load the form into examFormContainer
function loadExamForm(examType, examData = null) {
    const container = document.getElementById('examFormContainer');
    let formHTML = '';

    switch (examType) {
        case 'vital':
            formHTML = `
        <form id="examForm" data-type="vital" data-exam-id="${examData ? examData.id || '' : ''}">
          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label">Nhịp tim (lần/phút)</label>
              <input type="number" class="form-control" name="pulseRate" value="${examData ? examData.pulseRate || '' : ''}" placeholder="e.g., 80">
            </div>
            <div class="col-md-4">
              <label class="form-label">Huyết áp tâm thu (mmHg)</label>
              <input type="number" class="form-control" name="bpSystolic" value="${examData ? examData.bpSystolic || '' : ''}" placeholder="e.g., 120">
            </div>
            <div class="col-md-4">
              <label class="form-label">Huyết áp tâm trương (mmHg)</label>
              <input type="number" class="form-control" name="bpDiastolic" value="${examData ? examData.bpDiastolic || '' : ''}" placeholder="e.g., 80">
            </div>
            <div class="col-md-4">
              <label class="form-label">Nhiệt độ (°C)</label>
              <input type="number" step="0.1" class="form-control" name="temperature" value="${examData ? examData.temperature || '' : ''}" placeholder="e.g., 36.5">
            </div>
            <div class="col-md-4">
              <label class="form-label">Nhịp thở (lần/phút)</label>
              <input type="number" class="form-control" name="respiratoryRate" value="${examData ? examData.respiratoryRate || '' : ''}" placeholder="e.g., 16">
            </div>
            <div class="col-md-4">
              <label class="form-label">SpO2 (%)</label>
              <input type="number" class="form-control" name="spo2" value="${examData ? examData.spo2 || '' : ''}" placeholder="e.g., 98">
            </div>
          </div>
        </form>
      `;
            break;

        case 'respiratory':
            formHTML = `
        <form id="examForm" data-type="respiratory" data-exam-id="${examData ? examData.id || '' : ''}">
          <div class="mb-3">
            <label class="form-label">Kiểu thở</label>
            <input type="text" class="form-control" name="breathingPattern" value="${examData && examData.breathingPattern ? examData.breathingPattern : ''}" placeholder="e.g., Bình thường, khó thở">
          </div>
          <div class="mb-3">
            <label class="form-label">Rung thanh</label>
            <input type="text" class="form-control" name="fremitus" value="${examData && examData.fremitus ? examData.fremitus : ''}" placeholder="e.g., Bình thường, tăng">
          </div>
          <div class="mb-3">
            <label class="form-label">Âm gõ</label>
            <input type="text" class="form-control" name="percussionNote" value="${examData && examData.percussionNote ? examData.percussionNote : ''}" placeholder="e.g., Trong, đục">
          </div>
          <div class="mb-3">
            <label class="form-label">Kết quả nghe phổi</label>
            <textarea class="form-control" name="auscultation">${examData && examData.auscultation ? examData.auscultation : ''}</textarea>
          </div>
        </form>
      `;
            break;
        case 'cardiac':
            formHTML = `
        <form id="examForm" data-type="cardiac" data-exam-id="${examData ? examData.id || '' : ''}">
          <div class="mb-3">
            <label class="form-label">Nhịp tim (lần/phút)</label>
            <input type="number" class="form-control" name="heartRate" value="${examData && examData.heartRate ? examData.heartRate : ''}" placeholder="e.g., 80">
          </div>
          <div class="mb-3">
            <label class="form-label">Âm tim</label>
            <input type="text" class="form-control" name="heartSounds" value="${examData && examData.heartSounds ? examData.heartSounds : ''}" placeholder="e.g., S1, S2 bình thường">
          </div>
          <div class="mb-3">
            <label class="form-label">Tiếng thổi</label>
            <input type="text" class="form-control" name="murmur" value="${examData && examData.murmur ? examData.murmur : ''}" placeholder="e.g., Không có, systolic murmur">
          </div>
          <div class="mb-3">
            <label class="form-label">Áp lực tĩnh mạch cảnh</label>
            <input type="text" class="form-control" name="jugularVenousPressure" value="${examData && examData.jugularVenousPressure ? examData.jugularVenousPressure : ''}" placeholder="e.g., Bình thường, tăng">
          </div>
          <div class="mb-3">
            <label class="form-label">Phù</label>
            <input type="text" class="form-control" name="edema" value="${examData && examData.edema ? examData.edema : ''}" placeholder="e.g., Không có, phù chân">
          </div>
        </form>
      `;
            break;
        // Add other exam types as needed
    }

    container.innerHTML = formHTML;
}

async function fetchExams() {
    try {
        // Fetch vital signs
        const vitalResponse = await fetch(`/api/vital/${medicalRecordId}`);
        const vitalSigns = vitalResponse.ok ? await vitalResponse.json() : [];

        // Fetch respiratory exams
        const respiratoryResponse = await fetch(`/api/respiratory/${medicalRecordId}`);
        const respiratoryExams = respiratoryResponse.ok ? await respiratoryResponse.json() : [];

        // Fetch cardiac exams
        const cardiacResponse = await fetch(`/api/cardiac/${medicalRecordId}`);
        const cardiacExams = cardiacResponse.ok ? await cardiacResponse.json() : [];

        const examList = document.getElementById('examList');
        examList.innerHTML = ''; // Clear existing content

        // Display vital signs
        vitalSigns.forEach(vs => {
            const card = `
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-header" style="margin-bottom: 0.3rem;">
                            <h5>Dấu hiệu sinh tồn</h5>
                        </div>
                        <div class="card-body" style="padding: 18px 25px;">
                            <p><strong>Nhịp tim:</strong> ${vs.pulseRate || '-'} lần/phút</p>
                            <p><strong>Huyết áp:</strong> ${vs.bpSystolic || '-'} / ${vs.bpDiastolic || '-'} mmHg</p>
                            <p><strong>Nhiệt độ:</strong> ${vs.temperature || '-'} °C</p>
                            <p><strong>Nhịp thở:</strong> ${vs.respiratoryRate || '-'} lần/phút</p>
                            <p><strong>SpO2:</strong> ${vs.spo2 || '-'} %</p>
                            <p><strong>Thời gian:</strong> ${new Date(vs.recordedAt).toLocaleString('vi-VN')}</p>
                        </div>
                        <div class="card-footer" style="padding: 0px 0px 22px 140px;">
                            <button class="btn btn-sm btn-warning" style="padding: 6px 20px;margin-right: 10px;" onclick='openExamModal("vital", ${JSON.stringify(vs)})'>Sửa</button>
                            <button class="btn btn-sm btn-danger" style="padding: 6px 20px;margin-right: 10px;" onclick="deleteExam('vital', ${vs.id})">Xóa</button>
                        </div>
                    </div>
                </div>
            `;
            examList.innerHTML += card;
        });

        // Display respiratory exams
        respiratoryExams.forEach(re => {
            const card = `
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-header" style="margin-bottom: 0.3rem;">
                            <h5>Khám hô hấp</h5>
                        </div>
                        <div class="card-body" style="padding: 18px 25px;">
                            <p><strong>Kiểu thở:</strong> ${re.breathingPattern || '-'}</p>
                            <p><strong>Rung thanh:</strong> ${re.fremitus || '-'}</p>
                            <p><strong>Âm gõ:</strong> ${re.percussionNote || '-'}</p>
                            <p><strong>Kết quả nghe phổi:</strong> ${re.auscultation || '-'}</p>
                            <p><strong>Thời gian:</strong> ${new Date(re.recordedAt).toLocaleString('vi-VN')}</p>
                        </div>
                        <div class="card-footer" style="padding: 0px 0px 22px 140px;">
                            <button class="btn btn-sm btn-warning" style="padding: 6px 20px;margin-right: 10px;" onclick='openExamModal("respiratory", ${JSON.stringify(re)})'>Sửa</button>
                            <button class="btn btn-sm btn-danger" style="padding: 6px 20px;margin-right: 10px;" onclick="deleteExam('respiratory', ${re.id})">Xóa</button>
                        </div>
                    </div>
                </div>
            `;
            examList.innerHTML += card;
            cardiacExams.forEach(ce => {
                const card = `
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-header" style="margin-bottom: 0.3rem;"> 
                            <h5>Khám tim mạch</h5>
                        </div>
                        <div class="card-body" style="padding: 18px 25px;">
                            <p><strong>Nhịp tim:</strong> ${ce.heartRate || '-'} lần/phút</p>
                            <p><strong>Âm tim:</strong> ${ce.heartSounds || '-'}</p>
                            <p><strong>Tiếng thổi:</strong> ${ce.murmur || '-'}</p>
                            <p><strong>Áp lực tĩnh mạch cảnh:</strong> ${ce.jugularVenousPressure || '-'}</p>
                            <p><strong>Phù:</strong> ${ce.edema || '-'}</p>
                            <p><strong>Thời gian:</strong> ${new Date(ce.recordedAt).toLocaleString('vi-VN')}</p>
                        </div>
                        <div class="card-footer" style="padding: 0px 0px 22px 140px;">
                            <button class="btn btn-sm btn-warning" style="padding: 6px 20px;margin-right: 10px;" onclick='openExamModal("cardiac", ${JSON.stringify(ce)})'>Sửa</button>
                            <button class="btn btn-sm btn-danger" style="padding: 6px 20px;margin-right: 10px;" onclick="deleteExam('cardiac', ${ce.id})">Xóa</button>
                        </div>
                    </div>
                </div>
            `;
                examList.innerHTML += card;
            });
        });
    } catch (error) {
        console.error('Error:', error);
        alert('Đã xảy ra lỗi khi tải danh sách khám.');
    }
}

// Save or update exam
async function saveExam() {
    const form = document.getElementById('examForm');
    if (!form) {
        alert('Không tìm thấy form!');
        return;
    }

    const formType = form.getAttribute('data-type');
    const examId = form.getAttribute('data-exam-id');
    const formData = new FormData(form);
    const data = {};

    // Collect form data
    formData.forEach((value, key) => {
        if (value.trim() === '') {
            data[key] = null;
        } else if (formType === 'vital' && key === 'temperature') {
            data[key] = parseFloat(value);
        } else if ((formType === 'vital' && ['pulseRate', 'bpSystolic', 'bpDiastolic', 'respiratoryRate', 'spo2'].includes(key)) ||
            (formType === 'cardiac' && key === 'heartRate')) {
            data[key] = parseInt(value);
        } else {
            data[key] = value;
        }
    });

    // Client-side validation
    if (formType === 'vital') {
        if (data.pulseRate && (data.pulseRate < 30 || data.pulseRate > 200)) {
            alert('Nhịp tim phải trong khoảng 30-200 lần/phút.');
            return;
        }
        if (data.bpSystolic && (data.bpSystolic < 50 || data.bpSystolic > 250)) {
            alert('Huyết áp tâm thu phải trong khoảng 50-250 mmHg.');
            return;
        }
        if (data.bpDiastolic && (data.bpDiastolic < 30 || data.bpDiastolic > 150)) {
            alert('Huyết áp tâm trương phải trong khoảng 30-150 mmHg.');
            return;
        }
        if (data.temperature && (data.temperature < 34.0 || data.temperature > 42.0)) {
            alert('Nhiệt độ phải trong khoảng 34.0-42.0 °C.');
            return;
        }
        if (data.respiratoryRate && (data.respiratoryRate < 10 || data.respiratoryRate > 60)) {
            alert('Nhịp thở phải trong khoảng 10-60 lần/phút.');
            return;
        }
        if (data.spo2 && (data.spo2 < 70 || data.spo2 > 100)) {
            alert('SpO2 phải trong khoảng 70-100%.');
            return;
        }
    } else if (formType === 'cardiac') {
        if (data.heartRate && (data.heartRate < 30 || data.heartRate > 200)) {
            alert('Nhịp tim phải trong khoảng 30-200 lần/phút.');
            return;
        }
    }

    try {
        const isUpdate = examId && examId !== '';
        const method = isUpdate ? 'PUT' : 'POST';
        const endpoint = isUpdate
            ? `/api/${formType}/${examId}`
            : `/api/${formType}/${medicalRecordId}/add`;

        const response = await fetch(endpoint, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if (response.ok) {
            alert(isUpdate ? 'Cập nhật thành công!' : 'Thêm thành công!');
            const modal = bootstrap.Modal.getInstance(document.getElementById('examDetailModal'));
            modal.hide();
            form.reset();
            fetchExams(); // Refresh the list
        } else {
            const errorText = await response.text();
            alert(`Lưu thất bại: ${errorText || 'Lỗi không xác định'}`);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Đã xảy ra lỗi khi lưu kết quả.');
    }
}

async function deleteExam(examType, examId) {
    if (!confirm(`Bạn có chắc muốn xóa ${examType === 'vital' ? 'dấu hiệu sinh tồn' : examType === 'respiratory' ? 'khám hô hấp' : 'khám tim mạch'} này?`)) return;

    try {
        const response = await fetch(`/api/${examType}/${examId}`, {
            method: 'DELETE',
        });

        if (response.ok) {
            alert('Xóa thành công!');
            fetchExams(); // Refresh the list
        } else {
            alert('Xóa thất bại.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Đã xảy ra lỗi khi xóa.');
    }
}
