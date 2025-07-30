package org.project.service.impl;

import org.project.entity.*;
import org.project.model.dto.*;
import org.project.repository.*;
import org.project.service.ExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@Transactional
public class ExaminationServiceImpl implements ExaminationService {

    @Autowired
    private VitalSignsRepository vitalSignsRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private RespiratoryRepository respiratoryRepository;

    @Autowired
    private CardiacRepostiory cardiacRepostiory;

    @Autowired
    private NeurologicRepository neurologicRepository;

    @Autowired
    private GastrointestinalRepostiroy gastrointestinalRepostiroy;

    @Autowired
    private GenitourinaryRepository genitourinaryRepository;

    @Autowired
    private MusculoskeletalRepository musculoskeletalRepository;

    @Autowired
    private DermatologicExamRepository dermatologicExamRepository;

    @Override
    public Boolean addVitalSign(VitalSignDTO vitalSignDTO) {
        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        AppointmentEntity appointmentEntity = appointmentRepository.findById(vitalSignDTO.getAppointmentId()).get();
        VitalSignEntity vitalSignEntity = new VitalSignEntity();
        vitalSignEntity.setSpo2(30);
        vitalSignEntity.setPulseRate(vitalSignDTO.getPulseRate());
        vitalSignEntity.setBpDiastolic(vitalSignDTO.getBpDiastolic());
        vitalSignEntity.setRespiratoryRate(vitalSignDTO.getRespiratoryRate());
        vitalSignEntity.setTemperature(BigDecimal.valueOf(vitalSignDTO.getTemperature()));
        vitalSignEntity.setRecordedAt(vietnamTime.toInstant());
        vitalSignEntity.setMedicalRecord(appointmentEntity.getMedicalRecordEntities());
        vitalSignEntity.setBpSystolic(vitalSignDTO.getBpSystolic());
        vitalSignsRepository.save(vitalSignEntity);
        return true;
    }

    @Override
    public Boolean addRespiratory(RespiratoryDTO respiratoryDTO) {
        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        AppointmentEntity appointmentEntity = appointmentRepository.findById(respiratoryDTO.getAppointmentId()).get();
        RespiratoryExamEntity respiratoryExamEntity = new RespiratoryExamEntity();
        respiratoryExamEntity.setAuscultation(respiratoryDTO.getAuscultation());
        respiratoryExamEntity.setFremitus(respiratoryDTO.getFremitus());
        respiratoryExamEntity.setRecordedAt(vietnamTime.toInstant());
        respiratoryExamEntity.setMedicalRecord(appointmentEntity.getMedicalRecordEntities());
        respiratoryExamEntity.setBreathingPattern(respiratoryDTO.getBreathingPattern());
        respiratoryExamEntity.setPercussionNote(respiratoryDTO.getPurcussionNote());
        respiratoryRepository.save(respiratoryExamEntity);
        return true;
    }

    @Override
    public Boolean addCardiacExam(CardiacDTO cardiacDTO) {
        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        AppointmentEntity appointmentEntity = appointmentRepository.findById(cardiacDTO.getAppointmentId()).get();
        CardiacExamEntity cardiacExamEntity = new CardiacExamEntity();
        cardiacExamEntity.setEdema(cardiacDTO.getEdema());
        cardiacExamEntity.setMedicalRecord(appointmentEntity.getMedicalRecordEntities());
        cardiacExamEntity.setJugularVenousPressure(cardiacDTO.getJugularVenousPressure());
        cardiacExamEntity.setHeartRate(cardiacDTO.getHeartRate());
        cardiacExamEntity.setMurmur(cardiacDTO.getMurmur());
        cardiacExamEntity.setHeartSounds(cardiacDTO.getHeartSound());
        cardiacExamEntity.setRecordedAt(vietnamTime.toInstant());
        cardiacRepostiory.save(cardiacExamEntity);
        return true;
    }

    @Override
    public Boolean addNeurologic(NeurologicDTO neurologicDTO) {
        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        AppointmentEntity appointmentEntity = appointmentRepository.findById(neurologicDTO.getAppointmentId()).get();
        NeurologicExamEntity neurologicExamEntity = new NeurologicExamEntity();
        neurologicExamEntity.setConsciousness(neurologicDTO.getConsciousness());
        neurologicExamEntity.setMedicalRecord(appointmentEntity.getMedicalRecordEntities());
        neurologicExamEntity.setRecordedAt(vietnamTime.toInstant());
        neurologicExamEntity.setMotorFunction(neurologicDTO.getMotorFunction());
        neurologicExamEntity.setSensoryFunction(neurologicDTO.getSensoryFunction());
        neurologicExamEntity.setCranialNerves(neurologicDTO.getCranialNerves());
        neurologicExamEntity.setReflexes(neurologicDTO.getReflexes());
        neurologicRepository.save(neurologicExamEntity);
        return true;
    }

    @Override
    public Boolean addGastrointestinal(GastrointestinalDTO gastrointestinalDTO) {
        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        AppointmentEntity appointmentEntity = appointmentRepository.findById(gastrointestinalDTO.getAppointmentId()).get();
        GastrointestinalExam gastrointestinalExam = new GastrointestinalExam();
        gastrointestinalExam.setAuscultation(gastrointestinalDTO.getAuscultation());
        gastrointestinalExam.setMedicalRecord(appointmentEntity.getMedicalRecordEntities());
        gastrointestinalExam.setPalpation(gastrointestinalDTO.getPalpation());
        gastrointestinalExam.setPercussion(gastrointestinalDTO.getPercussion());
        gastrointestinalExam.setAbdominalInspection(gastrointestinalDTO.getAbdominalInspection());
        gastrointestinalExam.setRecordedAt(vietnamTime.toInstant());
        gastrointestinalRepostiroy.save(gastrointestinalExam);
        return true;
    }

    @Override
    public Boolean addGenitourinary(GenitourinaryDTO genitourinaryDTO) {
        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        AppointmentEntity appointmentEntity = appointmentRepository.findById(genitourinaryDTO.getAppointmentId()).get();
        GenitourinaryExam genitourinaryExam = new GenitourinaryExam();
        genitourinaryExam.setBladder(genitourinaryDTO.getBladder());
        genitourinaryExam.setRecordedAt(vietnamTime.toInstant());
        genitourinaryExam.setMedicalRecord(appointmentEntity.getMedicalRecordEntities());
        genitourinaryExam.setGenitalInspection(genitourinaryDTO.getGenitalInspection());
        genitourinaryExam.setKidneyArea(genitourinaryDTO.getKidneyArea());
        genitourinaryRepository.save(genitourinaryExam);
        return true;
    }

    @Override
    public Boolean addMusculoskeletal(MusculoskeletalDTO musculoskeletalDTO) {
        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        AppointmentEntity appointmentEntity = appointmentRepository.findById(musculoskeletalDTO.getAppointmentId()).get();
        MusculoskeletalExamEntity musculoskeletalExamEntity = new MusculoskeletalExamEntity();
        musculoskeletalExamEntity.setDeformity(musculoskeletalDTO.getDeformity());
        musculoskeletalExamEntity.setRecordedAt(vietnamTime.toInstant());
        musculoskeletalExamEntity.setMedicalRecord(appointmentEntity.getMedicalRecordEntities());
        musculoskeletalExamEntity.setJointExam(musculoskeletalDTO.getJointExam());
        musculoskeletalExamEntity.setMuscleStrength(musculoskeletalDTO.getMuscleStrength());
        musculoskeletalRepository.save(musculoskeletalExamEntity);
        return true;
    }

    @Override
    public Boolean addDermatologic(DermatologicDTO dermatologicDTO) {
        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        AppointmentEntity appointmentEntity = appointmentRepository.findById(dermatologicDTO.getAppointmentId()).get();
        DermatologicExamEntity dermatologicExamEntity = new DermatologicExamEntity();
        dermatologicExamEntity.setMedicalRecord(appointmentEntity.getMedicalRecordEntities());
        dermatologicExamEntity.setRecordedAt(vietnamTime.toInstant());
        dermatologicExamEntity.setRash(dermatologicDTO.getRash());
        dermatologicExamEntity.setLesions(dermatologicDTO.getLesions());
        dermatologicExamEntity.setSkinAppearance(dermatologicDTO.getSkinAppearance());
        dermatologicExamRepository.save(dermatologicExamEntity);
        return true;
    }
}
