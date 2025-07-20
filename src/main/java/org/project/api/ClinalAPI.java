package org.project.api;

import org.project.model.request.*;
import org.project.model.response.*;
import org.project.repository.GenitourinaryExamRepository;
import org.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ClinalAPI {
    @Autowired
    private VitalSignService vitalSignService;
    @Autowired
    private RespiratoryExamService respiratoryExamService;

    //Vital Sign
    @GetMapping("/vital/{medicalRecordId}")
    public ResponseEntity<List<VitalSignResponse>> getVitalSigns(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(vitalSignService.getVitalSign(medicalRecordId));
    }
    @PostMapping("/vital/{medicalRecordId}/add")
    public ResponseEntity<Boolean> addVitalSign(@PathVariable Long medicalRecordId, @RequestBody AddVitalSignRequest addVitalSignRequest) {
        return ResponseEntity.ok(vitalSignService.addVitalSign(medicalRecordId,addVitalSignRequest));
    }
    @PutMapping("/vital/{vitalSignId}")
    public ResponseEntity<Boolean> updateVitalSign(@PathVariable Long vitalSignId, @RequestBody AddVitalSignRequest request) {
        return ResponseEntity.ok(vitalSignService.updateVitalSign(vitalSignId,request));
    }
    @DeleteMapping("/vital/{vitalSignId}")
    public ResponseEntity<Boolean> deleteVitalSign(@PathVariable Long vitalSignId) {
        return ResponseEntity.ok(vitalSignService.deleteVitalSign(vitalSignId));
    }


    //Respiratory
    @GetMapping("/respiratory/{medicalRecordId}")
    public ResponseEntity<List<RespiratoryResponse>> getRespiratory(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(respiratoryExamService.getRespiratory(medicalRecordId));
    }
    @PostMapping("/respiratory/{medicalRecordId}/add")
    public ResponseEntity<Boolean> addRespiratory(@PathVariable Long medicalRecordId, @RequestBody AddRespiratoryRequest addRespiratoryRequest) {
        return ResponseEntity.ok(respiratoryExamService.addRespiratory(medicalRecordId,addRespiratoryRequest));
    }
    @PutMapping("/respiratory/{respiratoryId}")
    public ResponseEntity<Boolean> updateRespiratory(@PathVariable Long respiratoryId, @RequestBody AddRespiratoryRequest request) {
        return ResponseEntity.ok(respiratoryExamService.updateRespiratory(respiratoryId,request));
    }
    @DeleteMapping("/respiratory/{respiratoryId}")
    public ResponseEntity<Boolean> deleteRespiratory(@PathVariable Long respiratoryId) {
        return ResponseEntity.ok(respiratoryExamService.deleteRespiratory(respiratoryId));
    }

    //Cardiac
    @Autowired
    private CardiacExamService cardiacExamService;

    @GetMapping("/cardiac/{medicalRecordId}")
    public ResponseEntity<List<CardiacResponse>> getCardiac(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(cardiacExamService.getCardiac(medicalRecordId));
    }
    @PostMapping("/cardiac/{medicalRecordId}/add")
    public ResponseEntity<Boolean> addCardiac(@PathVariable Long medicalRecordId, @RequestBody AddCardiac addCardiac) {
        return ResponseEntity.ok(cardiacExamService.addCardiac(medicalRecordId,addCardiac));
    }
    @PutMapping("/cardiac/{cardiacId}")
    public ResponseEntity<Boolean> updateCardiac(@PathVariable Long cardiacId, @RequestBody AddCardiac addCardiac) {
        return ResponseEntity.ok(cardiacExamService.updateCardiac(cardiacId,addCardiac));
    }
    @DeleteMapping("/cardiac/{cardiacId}")
    public ResponseEntity<Boolean> deleteCardiac(@PathVariable Long cardiacId) {
        return ResponseEntity.ok(cardiacExamService.deleteCardiac(cardiacId));
    }
    @Autowired
    private NeurologicExamService neurologicExamService;
    //Neurologic
    @GetMapping("/neurologic/{medicalRecordId}")
    public ResponseEntity<List<NeurologicResponse>> getNeurologic(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(neurologicExamService.getNeurologic(medicalRecordId));
    }
    @PostMapping("/neurologic/{medicalRecordId}/add")
    public ResponseEntity<Boolean> addNeurologic(@PathVariable Long medicalRecordId, @RequestBody AddNeurologicRequest addNeurologicRequest) {
        return ResponseEntity.ok(neurologicExamService.addNeurologic(medicalRecordId,addNeurologicRequest));
    }
    @PutMapping("/neurologic/{neurologicId}")
    public ResponseEntity<Boolean> updateNeurologic(@PathVariable Long neurologicId, @RequestBody AddNeurologicRequest addNeurologicRequest) {
        return ResponseEntity.ok(neurologicExamService.updateNeurologic(neurologicId,addNeurologicRequest));
    }
    @DeleteMapping("/neurologic/{neurologicId}")
    public ResponseEntity<Boolean> deleteNeurologic(@PathVariable Long neurologicId) {
        return ResponseEntity.ok(neurologicExamService.deleteNeurologic(neurologicId));
    }
    //Gastro
    @Autowired
    private GastrointestinalExamService gastrointestinalExamService;
    @GetMapping("/gastro/{medicalRecordId}")
    public ResponseEntity<List<GastrointestinalResponse>> getGastro(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(gastrointestinalExamService.getGastrointestinal(medicalRecordId));
    }
    @PostMapping("/gastro/{medicalRecordId}/add")
    public ResponseEntity<Boolean> addGastro(@PathVariable Long medicalRecordId, @RequestBody AddGastrointestinalRequest addGastrointestinalRequest) {
        return ResponseEntity.ok(gastrointestinalExamService.addGastrointestinal(medicalRecordId,addGastrointestinalRequest));
    }
    @PutMapping("/gastro/{gastroId}")
    public ResponseEntity<Boolean> updateGastro(@PathVariable Long gastroId, @RequestBody AddGastrointestinalRequest addGastrointestinalRequest) {
        return ResponseEntity.ok(gastrointestinalExamService.updateGastrointestinal(gastroId,addGastrointestinalRequest));
    }
    @DeleteMapping("/gastro/{gastroId}")
    public ResponseEntity<Boolean> deleteGastro(@PathVariable Long gastroId) {
        return ResponseEntity.ok(gastrointestinalExamService.deleteGastrointestinal(gastroId));
    }
    //Genitourinary
    @Autowired
    private GenitourinaryExamService genitourinaryExamService;
    @GetMapping("/genitourinary/{medicalRecordId}")
    public ResponseEntity<List<GenitourinaryResponse>> getGenitourinary(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(genitourinaryExamService.getGenitourinary(medicalRecordId));
    }
    @PostMapping("/genitourinary/{medicalRecordId}/add")
    public ResponseEntity<Boolean> addGenitourinary(@PathVariable Long medicalRecordId, @RequestBody AddGenitourinaryRequest addGenitourinaryRequest) {
        return ResponseEntity.ok(genitourinaryExamService.addGenitourinary(medicalRecordId,addGenitourinaryRequest));
    }
    @PutMapping("/genitourinary/{genitourinaryId}")
    public ResponseEntity<Boolean> updateGenitourinary(@PathVariable Long genitourinaryId, @RequestBody AddGenitourinaryRequest addGenitourinaryRequest) {
        return ResponseEntity.ok(genitourinaryExamService.updateGenitourinary(genitourinaryId,addGenitourinaryRequest));
    }
    @DeleteMapping("/genitourinary/{genitourinaryId}")
    public ResponseEntity<Boolean> deleteGenitourinary(@PathVariable Long genitourinaryId) {
        return ResponseEntity.ok(genitourinaryExamService.deleteGenitourinary(genitourinaryId));
    }
    //Musculoskeletal
    @Autowired
    private MusculoskeletalExamService musculoskeletalExamService;
    @GetMapping("/musculoskeletal/{medicalRecordId}")
    public ResponseEntity<List<MusculoskeletalResponse>> getMusculoskeletal(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(musculoskeletalExamService.getMusculoskeletal(medicalRecordId));
    }
    @PostMapping("/musculoskeletal/{medicalRecordId}/add")
    public ResponseEntity<Boolean> addMusculoskeletal(@PathVariable Long medicalRecordId, @RequestBody AddMusculoskeletalRequest addMusculoskeletalRequest) {
        return ResponseEntity.ok(musculoskeletalExamService.addMusculoskeletal(medicalRecordId,addMusculoskeletalRequest));
    }
    @PutMapping("/musculoskeletal/{musculoskeletalId}")
    public ResponseEntity<Boolean> updateMusculoskeletal(@PathVariable Long musculoskeletalId, @RequestBody AddMusculoskeletalRequest addMusculoskeletalRequest) {
        return ResponseEntity.ok(musculoskeletalExamService.updateMusculoskeletal(musculoskeletalId,addMusculoskeletalRequest));
    }
    @DeleteMapping("/musculoskeletal/{musculoskeletalId}")
    public ResponseEntity<Boolean> deleteMusculoskeletal(@PathVariable Long musculoskeletalId) {
        return ResponseEntity.ok(musculoskeletalExamService.deleteMusculoskeletal(musculoskeletalId));
    }
    //Dermatologic
    @Autowired
    private DermatologicExamService dermatologicExamService;
    @GetMapping("/dermatologic/{medicalRecordId}")
    public ResponseEntity<List<DermatologicResponse>> getDermatologic(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(dermatologicExamService.getDermatologic(medicalRecordId));
    }
    @PostMapping("/dermatologic/{medicalRecordId}/add")
    public ResponseEntity<Boolean> addDermatologic(@PathVariable Long medicalRecordId, @RequestBody AddDermatologicRequest addDermatologicRequest) {
        return ResponseEntity.ok(dermatologicExamService.addDermatologic(medicalRecordId,addDermatologicRequest));
    }
    @PutMapping("/dermatologic/{dermatologicId}")
    public ResponseEntity<Boolean> updateDermatologic(@PathVariable Long dermatologicId, @RequestBody AddDermatologicRequest addDermatologicRequest) {
        return ResponseEntity.ok(dermatologicExamService.updateDermatologic(dermatologicId,addDermatologicRequest));
    }
    @DeleteMapping("/dermatologic/{dermatologicId}")
    public ResponseEntity<Boolean> deleteDermatologic(@PathVariable Long dermatologicId) {
        return ResponseEntity.ok(dermatologicExamService.deleteDermatologic(dermatologicId));
    }
    //Notes
    @Autowired
    private ClinicalNoteService clinicalNoteService;
    @GetMapping("/notes/{medicalRecordId}")
    public ResponseEntity<List<ClinalNoteResponse>> getNotes(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(clinicalNoteService.getClinicalNote(medicalRecordId));
    }
    @PostMapping("/notes/{medicalRecordId}/add")
    public ResponseEntity<Boolean> addNotes(@PathVariable Long medicalRecordId, @RequestBody AddClinalNoteRequest addClinalNoteRequest) {
        return ResponseEntity.ok(clinicalNoteService.addClinicalNote(medicalRecordId,addClinalNoteRequest));
    }
    @PutMapping("/notes/{notesId}")
    public ResponseEntity<Boolean> updateNotes(@PathVariable Long notesId, @RequestBody AddClinalNoteRequest addClinalNoteRequest) {
        return ResponseEntity.ok(clinicalNoteService.updateClinicalNote(notesId,addClinalNoteRequest));
    }
    @DeleteMapping("/notes/{notesId}")
    public ResponseEntity<Boolean> deleteNotes(@PathVariable Long notesId) {
        return ResponseEntity.ok(clinicalNoteService.deleteClinicalNote(notesId));
    }
}
