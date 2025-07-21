function savePreliminaryDiagnosis() {
    const diagnosisTextarea = document.getElementById('preliminaryDiagnosis');
    const diagnosis = diagnosisTextarea.value.trim();

    if (!diagnosis) {
        showToast('error',"Vui lòng nhập thông tin về chuẩn đoán ")
        diagnosisTextarea.classList.add('border', 'border-danger');
        return;
    }
    diagnosisTextarea.classList.remove('border', 'border-danger');
    const payload = {
        diagnosis: diagnosis
    };
    console.log(appointmentId)

    fetch(`/api/medical-record/${appointmentId}/diagnosis`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload)
    })
        .then(response => response.json()
        )
        .then(data => {
            showToast('success',"Cập nhật chuẩn đoán thành công")
            loadPreliminaryDiagnosis()
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function loadPreliminaryDiagnosis() {
    const diagnosisTextarea = document.getElementById('preliminaryDiagnosis');

    diagnosisTextarea.disabled = true;
    diagnosisTextarea.placeholder = 'Đang tải chẩn đoán...';

    fetch(`/api/medical-record/${appointmentId}/diagnosis`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load diagnosis');
            }
            return response.text();
        })
        .then(data => {
            diagnosisTextarea.value = data || '';
            diagnosisTextarea.placeholder = '';
            diagnosisTextarea.disabled = false;
        })
        .catch(error => {
            console.log(error)
        });
}

function saveTreatmentPlan() {
    const treatmentPlanTextarea = document.getElementById('treatmentPlan');
    const treatmentPlan = treatmentPlanTextarea.value.trim();

    if (!treatmentPlan) {
            showToast('error','Vui lòng nhập kế hoạch để hệ thống lưu thông tin');
        return;
    }

    const payload = {
        treatment_plan: treatmentPlan
    };

    fetch(`/api/medical-record/${appointmentId}/treatment-plan`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to save treatment plan');
            }
            return response.json();
        })
        .then(data => {
            showToast('success',"Cập nhật chuẩn đoán thành công")
            loadTreatmentPlan()
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function loadTreatmentPlan() {
    const treatmentPlanTextarea = document.getElementById('treatmentPlan');

    fetch(`/api/medical-record/${appointmentId}/plan`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load treatment plan');
            }
            return response.text(); // Use conservatism since endpoint returns a String
        })
        .then(data => {
            treatmentPlanTextarea.value = data || '';
            treatmentPlanTextarea.placeholder = '';
            console.log(data)
        })
        .catch(error => {
            console.error('Error:', error);
        });
}