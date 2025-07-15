async function loadMedicalProfile() {
    if (!patientId) return;
    try {
        const response = await fetch(`/api/medical-profile/${patientId}`);
        if (!response.ok) throw new Error('Lỗi tải dữ liệu');
        const data = await response.json();
        allergies = data.allergies || [];
        chronicDiseases = data.chronicDiseases || [];
        updateMedicalProfileUI();
    } catch (error) {
        console.error('Error:', error);
        showToast('error', 'Không thể tải thông tin hồ sơ y tế');
    }
}

async function loadSymptoms() {
    if (!medicalRecordId) {
        console.error('Medical record ID is not available');
        showToast('error', 'Không thể tải triệu chứng: Thiếu ID hồ sơ y tế');
        return;
    }
    try {
        const response = await fetch(`/api/medical-record-symptom/${medicalRecordId}`);
        if (!response.ok) throw new Error('Lỗi tải triệu chứng');
        symptoms = await response.json();
        updateMedicalProfileUI();
    } catch (error) {
        console.error('Error loading symptoms:', error);
        showToast('error', 'Không thể tải triệu chứng');
    }
}

function updateMedicalProfileUI() {
    // Update main complaint

    // Update allergies
    const allergiesContainer = document.getElementById('allergiesContainer');
    allergiesContainer.innerHTML = '';
    if (!allergies || allergies.length === 0) {
        allergiesContainer.innerHTML = '<span class="badge bg-light text-dark border">Không có dị ứng</span>';
    } else {
        allergies.forEach(allergy => {
            const span = document.createElement('span');
            span.className = 'badge bg-light text-dark border';
            span.textContent = allergy;
            allergiesContainer.appendChild(span);
        });
    }

    // Update chronic diseases
    const chronicDiseasesContainer = document.getElementById('chronicDiseasesContainer');
    chronicDiseasesContainer.innerHTML = '';
    if (!chronicDiseases || chronicDiseases.length === 0) {
        chronicDiseasesContainer.innerHTML = '<span class="badge bg-light text-dark border">Không có bệnh mãn tính</span>';
    } else {
        chronicDiseases.forEach(disease => {
            const span = document.createElement('span');
            span.className = 'badge bg-light text-dark border';
            span.textContent = disease;
            chronicDiseasesContainer.appendChild(span);
        });
    }

    // Update symptoms
    const symptomsContainer = document.getElementById('symptomsContainer');
    symptomsContainer.innerHTML = '';
    if (!symptoms || symptoms.length === 0) {
        symptomsContainer.innerHTML = '<div class="list-group-item text-muted">Chưa có triệu chứng nào</div>';
    } else {
        symptoms.forEach(symptom => {
            const div = document.createElement('div');
            div.className = 'list-group-item d-flex justify-content-between align-items-start flex-column flex-md-row';

            // Tạo phần mô tả triệu chứng
            const contentDiv = document.createElement('div');
            contentDiv.innerHTML = `
    <strong>${symptom.symptomName}</strong>
    ${symptom.duration ? ' - ' + symptom.duration : ''}
    ${symptom.severity ? ' (' + symptom.severity + ')' : ''}
    ${symptom.onsetDate ? ' - Bắt đầu: ' + symptom.onsetDate : ''}
    ${symptom.description ? '<br><small>' + symptom.description + '</small>' : ''}
  `;

            // Tạo phần chứa nút Sửa / Xóa
            const buttonDiv = document.createElement('div');
            buttonDiv.className = 'mt-2 mt-md-0 text-end';
            buttonDiv.innerHTML = `
    <button class="btn btn-sm btn-outline-primary me-2" onclick="editSymptom('${symptom.id}')">
      <i class="bi bi-pencil"></i> Sửa
    </button>
    <button class="btn btn-sm btn-outline-danger" onclick="deleteSymptom('${symptom.id}')">
      <i class="bi bi-trash"></i> Xóa
    </button>
  `;

            div.appendChild(contentDiv);
            div.appendChild(buttonDiv);
            symptomsContainer.appendChild(div);
        });
    }
}

function openEditAllergiesModal() {
    const modal = new bootstrap.Modal(document.getElementById('editAllergiesModal'));
    const input = document.getElementById('allergiesInput');
    input.value = allergies ? allergies.join(', ') : '';
    modal.show();
}

function openEditChronicModal() {
    const modal = new bootstrap.Modal(document.getElementById('editChronicModal'));
    const input = document.getElementById('chronicDiseasesInput');
    input.value = chronicDiseases ? chronicDiseases.join(', ') : '';
    modal.show();
}

function openSymptomModal(symptomId) {
    console.log(symptoms)
    const modalElement = document.getElementById('symptomModal');
    if (!modalElement) {
        console.error('Symptom modal not found');
        showToast('error', 'Không thể mở modal triệu chứng');
        return;
    }
    const modal = new bootstrap.Modal(modalElement);
    const modalTitle = document.getElementById('symptomModalTitle');
    const saveBtn = document.getElementById('saveSymptomBtn');
    const symptomNameInput = document.getElementById('symptomName');
    const durationInput = document.getElementById('symptomDuration');
    const severityInput = document.getElementById('symptomSeverity');
    const descriptionInput = document.getElementById('symptomDescription');
    const idInput = document.getElementById('symptomId');

    if (!modalTitle || !saveBtn || !symptomNameInput || !durationInput || !severityInput || !descriptionInput || !idInput) {
        console.error('One or more modal elements not found');
        showToast('error', 'Lỗi giao diện: Không tìm thấy các thành phần modal');
        return;
    }

    const symptom = symptoms.find(s => s.id == symptomId);
    console.log(symptom)

    if (symptomId) {
        modalTitle.textContent = 'Chỉnh sửa triệu chứng';
        saveBtn.textContent = 'Lưu thay đổi';
        idInput.value = symptom.id || '';
        symptomNameInput.value = symptom.symptomName || '';
        durationInput.value = symptom.duration || '';
        severityInput.value = symptom.severity || '';
        descriptionInput.value = symptom.description || '';
    } else {
        modalTitle.textContent = 'Thêm triệu chứng';
        saveBtn.textContent = 'Lưu triệu chứng';
        idInput.value = '';
        symptomNameInput.value = '';
        durationInput.value = '';
        severityInput.value = '';
        descriptionInput.value = '';
    }
    modal.show();
}

function editSymptom(symptomId) {
    console.log(symptomId)
    openSymptomModal(symptomId);
}

async function saveSymptom() {
    const symptomId = document.getElementById('symptomId').value;
    console.log(symptomId)
    const symptomName = document.getElementById('symptomName').value.trim();
    const duration = document.getElementById('symptomDuration').value.trim();
    const severity = document.getElementById('symptomSeverity').value;
    const description = document.getElementById('symptomDescription').value.trim();
    if (!symptomName) {
        showToast('error', 'Vui lòng nhập tên triệu chứng');
        return;
    }
    if (!medicalRecordId) {
        showToast('error', 'Không thể lưu triệu chứng: Thiếu ID hồ sơ y tế');
        return;
    }
    const symptom = { symptomName, duration, severity, description };

    try {
        console.log("vao day la "+symptomId)
        if (symptomId) {
            // Update symptom
            const response = await fetch(`/api/medical-record-symptom/${symptomId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(symptom)
            });
            if (!response.ok) throw new Error('Failed to update symptom');
            showToast('success', 'Cập nhật triệu chứng thành công');
        } else {
            // Add new symptom
            const response = await fetch(`/api/medical-record-symptom/${medicalRecordId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify([symptom])
            });
            if (!response.ok) throw new Error('Failed to add symptom');
            await response.json();
            showToast('success', 'Thêm triệu chứng thành công');
        }
        await loadSymptoms();
        bootstrap.Modal.getInstance(document.getElementById('symptomModal')).hide();
    } catch (error) {
        console.error('Error saving symptom:', error);
        showToast('error', `Không thể ${symptomId ? 'cập nhật' : 'thêm'} triệu chứng`);
    }
}

async function deleteSymptom(symptomId) {
    if (!confirm('Bạn có chắc muốn xóa triệu chứng này?')) return;
    try {
        const response = await fetch(`/api/medical-record-symptom/${symptomId}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error('Failed to delete symptom');
        await loadSymptoms();
        showToast('success', 'Xóa triệu chứng thành công');
    } catch (error) {
        console.error('Error deleting symptom:', error);
        showToast('error', 'Không thể xóa triệu chứng');
    }
}

async function saveAllergies() {
    const allergiesInput = document.getElementById('allergiesInput').value;
    const newAllergies = allergiesInput.split(',').map(item => item.trim()).filter(item => item);
    try {
        const response = await fetch(`/api/medical-profile/${patientId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ allergies: newAllergies, chronicDiseases })
        });
        if (!response.ok) throw new Error('Failed to update allergies');
        const updatedProfile = await response.json();
        allergies = updatedProfile.allergies || [];
        updateMedicalProfileUI();
        bootstrap.Modal.getInstance(document.getElementById('editAllergiesModal')).hide();
        showToast('success', 'Cập nhật dị ứng thành công');
    } catch (error) {
        console.error('Error updating allergies:', error);
        showToast('error', 'Không thể cập nhật dị ứng');
    }
}

async function saveChronicDiseases() {
    const chronicDiseasesInput = document.getElementById('chronicDiseasesInput').value;
    const newChronicDiseases = chronicDiseasesInput.split(',').map(item => item.trim()).filter(item => item);
    try {
        const response = await fetch(`/api/medical-profile/${patientId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ allergies, chronicDiseases: newChronicDiseases })
        });
        if (!response.ok) throw new Error('Failed to update chronic diseases');
        const updatedProfile = await response.json();
        chronicDiseases = updatedProfile.chronicDiseases || [];
        updateMedicalProfileUI();
        bootstrap.Modal.getInstance(document.getElementById('editChronicModal')).hide();
        showToast('success', 'Cập nhật bệnh mãn tính thành công');
    } catch (error) {
        console.error('Error updating chronic diseases:', error);
        showToast('error', 'Không thể cập nhật bệnh mãn tính');
    }
}