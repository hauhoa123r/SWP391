function openExamModal(examType, examData = null) {
    const examTypeModalElement = document.getElementById('examTypeModal');
    if (examTypeModalElement) {
        const examTypeModal = bootstrap.Modal.getInstance(examTypeModalElement);
        if (examTypeModal) {
            examTypeModal.hide();
        }
    }

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

    loadExamForm(examType, examData);

    new bootstrap.Modal(document.getElementById('examDetailModal')).show();
}


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

        case 'neurologic':
            formHTML = `
        <form id="examForm" data-type="neurologic" data-exam-id="${examData ? examData.id || '' : ''}">
          <div class="mb-3">
            <label class="form-label">Ý thức</label>
            <input type="text" class="form-control" name="consciousness" value="${examData && examData.consciousness ? examData.consciousness : ''}" placeholder="e.g., Tỉnh táo, lơ mơ">
          </div>
          <div class="mb-3">
            <label class="form-label">Thần kinh sọ</label>
            <input type="text" class="form-control" name="cranialNerves" value="${examData && examData.cranialNerves ? examData.cranialNerves : ''}" placeholder="e.g., Bình thường, bất thường">
          </div>
          <div class="mb-3">
            <label class="form-label">Chức năng vận động</label>
            <input type="text" class="form-control" name="motorFunction" value="${examData && examData.motorFunction ? examData.motorFunction : ''}" placeholder="e.g., Bình thường, yếu cơ">
          </div>
          <div class="mb-3">
            <label class="form-label">Chức năng cảm giác</label>
            <input type="text" class="form-control" name="sensoryFunction" value="${examData && examData.sensoryFunction ? examData.sensoryFunction : ''}" placeholder="e.g., Bình thường, giảm cảm giác">
          </div>
          <div class="mb-3">
            <label class="form-label">Phản xạ</label>
            <input type="text" class="form-control" name="reflexes" value="${examData && examData.reflexes ? examData.reflexes : ''}" placeholder="e.g., Bình thường, tăng phản xạ">
          </div>
        </form>
      `;
            break;

        case 'gastro':
            formHTML = `
        <form id="examForm" data-type="gastro" data-exam-id="${examData ? examData.id || '' : ''}">
          <div class="mb-3">
            <label class="form-label">Quan sát bụng</label>
            <input type="text" class="form-control" name="abdominalInspection" value="${examData && examData.abdominalInspection ? examData.abdominalInspection : ''}" placeholder="e.g., Bình thường, chướng bụng">
          </div>
          <div class="mb-3">
            <label class="form-label">Sờ nắn</label>
            <input type="text" class="form-control" name="palpation" value="${examData && examData.palpation ? examData.palpation : ''}" placeholder="e.g., Mềm, đau khi sờ">
          </div>
          <div class="mb-3">
            <label class="form-label">Gõ</label>
            <input type="text" class="form-control" name="percussion" value="${examData && examData.percussion ? examData.percussion : ''}" placeholder="e.g., Âm đục, âm vang">
          </div>
          <div class="mb-3">
            <label class="form-label">Nghe</label>
            <textarea class="form-control" name="auscultation">${examData && examData.auscultation ? examData.auscultation : ''}</textarea>
          </div>
        </form>
      `;
            break;

        case 'genitourinary':
            formHTML = `
        <form id="examForm" data-type="genitourinary" data-exam-id="${examData ? examData.id || '' : ''}">
          <div class="mb-3">
            <label class="form-label">Vùng thận</label>
            <input type="text" class="form-control" name="kidneyArea" value="${examData && examData.kidneyArea ? examData.kidneyArea : ''}" placeholder="e.g., Bình thường, đau khi gõ">
          </div>
          <div class="mb-3">
            <label class="form-label">Bàng quang</label>
            <input type="text" class="form-control" name="bladder" value="${examData && examData.bladder ? examData.bladder : ''}" placeholder="e.g., Bình thường, căng tức">
          </div>
          <div class="mb-3">
            <label class="form-label">Kiểm tra cơ quan sinh dục</label>
            <input type="text" class="form-control" name="genitalInspection" value="${examData && examData.genitalInspection ? examData.genitalInspection : ''}" placeholder="e.g., Bình thường, bất thường">
          </div>
        </form>
      `;
            break;

        case 'musculoskeletal':
            formHTML = `
        <form id="examForm" data-type="musculoskeletal" data-exam-id="${examData ? examData.id || '' : ''}">
          <div class="mb-3">
            <label class="form-label">Khám khớp</label>
            <input type="text" class="form-control" name="jointExam" value="${examData && examData.jointExam ? examData.jointExam : ''}" placeholder="e.g., Bình thường, sưng khớp">
          </div>
          <div class="mb-3">
            <label class="form-label">Sức mạnh cơ</label>
            <input type="text" class="form-control" name="muscleStrength" value="${examData && examData.muscleStrength ? examData.muscleStrength : ''}" placeholder="e.g., 5/5, giảm sức cơ">
          </div>
          <div class="mb-3">
            <label class="form-label">Dị dạng</label>
            <input type="text" class="form-control" name="deformity" value="${examData && examData.deformity ? examData.deformity : ''}" placeholder="e.g., Không có, vẹo cột sống">
          </div>
        </form>
      `;
            break;

        case 'dermatologic':
            formHTML = `
        <form id="examForm" data-type="dermatologic" data-exam-id="${examData ? examData.id || '' : ''}">
          <div class="mb-3">
            <label class="form-label">Tình trạng da</label>
            <input type="text" class="form-control" name="skinAppearance" value="${examData && examData.skinAppearance ? examData.skinAppearance : ''}" placeholder="e.g., Bình thường, khô da">
          </div>
          <div class="mb-3">
            <label class="form-label">Phát ban</label>
            <input type="text" class="form-control" name="rash" value="${examData && examData.rash ? examData.rash : ''}" placeholder="e.g., Không có, phát ban đỏ">
          </div>
          <div class="mb-3">
            <label class="form-label">Tổn thương</label>
            <input type="text" class="form-control" name="lesions" value="${examData && examData.lesions ? examData.lesions : ''}" placeholder="e.g., Không có, loét da">
          </div>
        </form>
      `;
            break;

        case 'notes':
            formHTML = `
        <form id="examForm" data-type="notes" data-exam-id="${examData ? examData.id || '' : ''}">
          <div class="mb-3">
            <label class="form-label">Ghi chú</label>
            <textarea class="form-control" name="noteText">${examData && examData.noteText ? examData.noteText : ''}</textarea>
          </div>
        </form>
      `;
            break;
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

        // Fetch neurologic exams
        const neurologicResponse = await fetch(`/api/neurologic/${medicalRecordId}`);
        const neurologicExams = neurologicResponse.ok ? await neurologicResponse.json() : [];

        // Fetch gastrointestinal exams
        const gastroResponse = await fetch(`/api/gastro/${medicalRecordId}`);
        const gastroExams = gastroResponse.ok ? await gastroResponse.json() : [];

        // Fetch genitourinary exams
        const genitourinaryResponse = await fetch(`/api/genitourinary/${medicalRecordId}`);
        const genitourinaryExams = genitourinaryResponse.ok ? await genitourinaryResponse.json() : [];

        // Fetch musculoskeletal exams
        const musculoskeletalResponse = await fetch(`/api/musculoskeletal/${medicalRecordId}`);
        const musculoskeletalExams = musculoskeletalResponse.ok ? await musculoskeletalResponse.json() : [];

        // Fetch dermatologic exams
        const dermatologicResponse = await fetch(`/api/dermatologic/${medicalRecordId}`);
        const dermatologicExams = dermatologicResponse.ok ? await dermatologicResponse.json() : [];

        // Fetch clinical notes
        const notesResponse = await fetch(`/api/notes/${medicalRecordId}`);
        const notes = notesResponse.ok ? await notesResponse.json() : [];

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
        });

        // Display cardiac exams
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

        // Display neurologic exams
        neurologicExams.forEach(ne => {
            const card = `
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-header" style="margin-bottom: 0.3rem;">
                            <h5>Khám thần kinh</h5>
                        </div>
                        <div class="card-body" style="padding: 18px 25px;">
                            <p><strong>Ý thức:</strong> ${ne.consciousness || '-'}</p>
                            <p><strong>Thần kinh sọ:</strong> ${ne.cranialNerves || '-'}</p>
                            <p><strong>Chức năng vận động:</strong> ${ne.motorFunction || '-'}</p>
                            <p><strong>Chức năng cảm giác:</strong> ${ne.sensoryFunction || '-'}</p>
                            <p><strong>Phản xạ:</strong> ${ne.reflexes || '-'}</p>
                            <p><strong>Thời gian:</strong> ${new Date(ne.recordedAt).toLocaleString('vi-VN')}</p>
                        </div>
                        <div class="card-footer" style="padding: 0px 0px 22px 140px;">
                            <button class="btn btn-sm btn-warning" style="padding: 6px 20px;margin-right: 10px;" onclick='openExamModal("neurologic", ${JSON.stringify(ne)})'>Sửa</button>
                            <button class="btn btn-sm btn-danger" style="padding: 6px 20px;margin-right: 10px;" onclick="deleteExam('neurologic', ${ne.id})">Xóa</button>
                        </div>
                    </div>
                </div>
            `;
            examList.innerHTML += card;
        });

        // Display gastrointestinal exams
        gastroExams.forEach(ge => {
            const card = `
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-header" style="margin-bottom: 0.3rem;">
                            <h5>Khám tiêu hóa</h5>
                        </div>
                        <div class="card-body" style="padding: 18px 25px;">
                            <p><strong>Quan sát bụng:</strong> ${ge.abdominalInspection || '-'}</p>
                            <p><strong>Sờ nắn:</strong> ${ge.palpation || '-'}</p>
                            <p><strong>Gõ:</strong> ${ge.percussion || '-'}</p>
                            <p><strong>Nghe:</strong> ${ge.auscultation || '-'}</p>
                            <p><strong>Thời gian:</strong> ${new Date(ge.recordedAt).toLocaleString('vi-VN')}</p>
                        </div>
                        <div class="card-footer" style="padding: 0px 0px 22px 140px;">
                            <button class="btn btn-sm btn-warning" style="padding: 6px 20px;margin-right: 10px;" onclick='openExamModal("gastro", ${JSON.stringify(ge)})'>Sửa</button>
                            <button class="btn btn-sm btn-danger" style="padding: 6px 20px;margin-right: 10px;" onclick="deleteExam('gastro', ${ge.id})">Xóa</button>
                        </div>
                    </div>
                </div>
            `;
            examList.innerHTML += card;
        });

        // Display genitourinary exams
        genitourinaryExams.forEach(gu => {
            const card = `
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-header" style="margin-bottom: 0.3rem;">
                            <h5>Khám tiết niệu</h5>
                        </div>
                        <div class="card-body" style="padding: 18px 25px;">
                            <p><strong>Vùng thận:</strong> ${gu.kidneyArea || '-'}</p>
                            <p><strong>Bàng quang:</strong> ${gu.bladder || '-'}</p>
                            <p><strong>Kiểm tra cơ quan sinh dục:</strong> ${gu.genitalInspection || '-'}</p>
                            <p><strong>Thời gian:</strong> ${new Date(gu.recordedAt).toLocaleString('vi-VN')}</p>
                        </div>
                        <div class="card-footer" style="padding: 0px 0px 22px 140px;">
                            <button class="btn btn-sm btn-warning" style="padding: 6px 20px;margin-right: 10px;" onclick='openExamModal("genitourinary", ${JSON.stringify(gu)})'>Sửa</button>
                            <button class="btn btn-sm btn-danger" style="padding: 6px 20px;margin-right: 10px;" onclick="deleteExam('genitourinary', ${gu.id})">Xóa</button>
                        </div>
                    </div>
                </div>
            `;
            examList.innerHTML += card;
        });

        // Display musculoskeletal exams
        musculoskeletalExams.forEach(me => {
            const card = `
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-header" style="margin-bottom: 0.3rem;">
                            <h5>Khám cơ xương</h5>
                        </div>
                        <div class="card-body" style="padding: 18px 25px;">
                            <p><strong>Khám khớp:</strong> ${me.jointExam || '-'}</p>
                            <p><strong>Sức mạnh cơ:</strong> ${me.muscleStrength || '-'}</p>
                            <p><strong>Dị dạng:</strong> ${me.deformity || '-'}</p>
                            <p><strong>Thời gian:</strong> ${new Date(me.recordedAt).toLocaleString('vi-VN')}</p>
                        </div>
                        <div class="card-footer" style="padding: 0px 0px 22px 140px;">
                            <button class="btn btn-sm btn-warning" style="padding: 6px 20px;margin-right: 10px;" onclick='openExamModal("musculoskeletal", ${JSON.stringify(me)})'>Sửa</button>
                            <button class="btn btn-sm btn-danger" style="padding: 6px 20px;margin-right: 10px;" onclick="deleteExam('musculoskeletal', ${me.id})">Xóa</button>
                        </div>
                    </div>
                </div>
            `;
            examList.innerHTML += card;
        });

        // Display dermatologic exams
        dermatologicExams.forEach(de => {
            const card = `
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-header" style="margin-bottom: 0.3rem;">
                            <h5>Khám da liễu</h5>
                        </div>
                        <div class="card-body" style="padding: 18px 25px;">
                            <p><strong>Tình trạng da:</strong> ${de.skinAppearance || '-'}</p>
                            <p><strong>Phát ban:</strong> ${de.rash || '-'}</p>
                            <p><strong>Tổn thương:</strong> ${de.lesions || '-'}</p>
                            <p><strong>Thời gian:</strong> ${new Date(de.recordedAt).toLocaleString('vi-VN')}</p>
                        </div>
                        <div class="card-footer" style="padding: 0px 0px 22px 140px;">
                            <button class="btn btn-sm btn-warning" style="padding: 6px 20px;margin-right: 10px;" onclick='openExamModal("dermatologic", ${JSON.stringify(de)})'>Sửa</button>
                            <button class="btn btn-sm btn-danger" style="padding: 6px 20px;margin-right: 10px;" onclick="deleteExam('dermatologic', ${de.id})">Xóa</button>
                        </div>
                    </div>
                </div>
            `;
            examList.innerHTML += card;
        });

        // Display clinical notes
        notes.forEach(nt => {
            const card = `
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-header" style="margin-bottom: 0.3rem;">
                            <h5>Ghi chú khác</h5>
                        </div>
                        <div class="card-body" style="padding: 18px 25px;">
                            <p><strong>Ghi chú:</strong> ${nt.noteText || '-'}</p>
                            <p><strong>Thời gian:</strong> ${new Date(nt.recordedAt).toLocaleString('vi-VN')}</p>
                        </div>
                        <div class="card-footer" style="padding: 0px 0px 22px 140px;">
                            <button class="btn btn-sm btn-warning" style="padding: 6px 20px;margin-right: 10px;" onclick='openExamModal("notes", ${JSON.stringify(nt)})'>Sửa</button>
                            <button class="btn btn-sm btn-danger" style="padding: 6px 20px;margin-right: 10px;" onclick="deleteExam('notes', ${nt.id})">Xóa</button>
                        </div>
                    </div>
                </div>
            `;
            examList.innerHTML += card;
        });
    } catch (error) {
        console.error('Error:', error);
        alert('Đã xảy ra lỗi khi tải danh sách khám.');
    }
}

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
    const typeNames = {
        vital: 'dấu hiệu sinh tồn',
        respiratory: 'khám hô hấp',
        cardiac: 'khám tim mạch',
        neurologic: 'khám thần kinh',
        gastro: 'khám tiêu hóa',
        genitourinary: 'khám tiết niệu',
        musculoskeletal: 'khám cơ xương',
        dermatologic: 'khám da liễu',
        notes: 'ghi chú khác'
    };
    if (!confirm(`Bạn có chắc muốn xóa ${typeNames[examType] || examType} này?`)) return;

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
