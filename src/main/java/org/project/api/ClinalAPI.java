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
//    //Musculoskeletal
//    @GetMapping("/cardiac/{medicalRecordId}")
//    public ResponseEntity<List<RespiratoryResponse>> getMusculoskeletal(@PathVariable Long medicalRecordId) {
//        return ResponseEntity.ok(respiratoryExamService.getRespiratory(medicalRecordId));
//    }
//    @PostMapping("/cardiac/{medicalRecordId}/add")
//    public ResponseEntity<Boolean> addMusculoskeletal(@PathVariable Long medicalRecordId, @RequestBody AddRespiratoryRequest addRespiratoryRequest) {
//        return ResponseEntity.ok(respiratoryExamService.addRespiratory(medicalRecordId,addRespiratoryRequest));
//    }
//    @PutMapping("/cardiac/{cardiacId}")
//    public ResponseEntity<Boolean> updateMusculoskeletal(@PathVariable Long cardiacId, @RequestBody AddRespiratoryRequest request) {
//        return ResponseEntity.ok(respiratoryExamService.updateRespiratory(respiratoryId,request));
//    }
//    @DeleteMapping("/cardiac/{cardiacId}")
//    public ResponseEntity<Boolean> deleteMusculoskeletal(@PathVariable Long cardiacId) {
//        return ResponseEntity.ok(respiratoryExamService.deleteRespiratory(respiratoryId));
//    }
//    //Dermatologic
//    @GetMapping("/cardiac/{medicalRecordId}")
//    public ResponseEntity<List<RespiratoryResponse>> getDermatologic(@PathVariable Long medicalRecordId) {
//        return ResponseEntity.ok(respiratoryExamService.getRespiratory(medicalRecordId));
//    }
//    @PostMapping("/cardiac/{medicalRecordId}/add")
//    public ResponseEntity<Boolean> addDermatologic(@PathVariable Long medicalRecordId, @RequestBody AddRespiratoryRequest addRespiratoryRequest) {
//        return ResponseEntity.ok(respiratoryExamService.addRespiratory(medicalRecordId,addRespiratoryRequest));
//    }
//    @PutMapping("/cardiac/{cardiacId}")
//    public ResponseEntity<Boolean> updateDermatologic(@PathVariable Long cardiacId, @RequestBody AddRespiratoryRequest request) {
//        return ResponseEntity.ok(respiratoryExamService.updateRespiratory(respiratoryId,request));
//    }
//    @DeleteMapping("/cardiac/{cardiacId}")
//    public ResponseEntity<Boolean> deleteDermatologic(@PathVariable Long cardiacId) {
//        return ResponseEntity.ok(respiratoryExamService.deleteRespiratory(respiratoryId));
//    }
//    //Notes
//    @GetMapping("/cardiac/{medicalRecordId}")
//    public ResponseEntity<List<RespiratoryResponse>> getNotes(@PathVariable Long medicalRecordId) {
//        return ResponseEntity.ok(respiratoryExamService.getRespiratory(medicalRecordId));
//    }
//    @PostMapping("/cardiac/{medicalRecordId}/add")
//    public ResponseEntity<Boolean> addNotes(@PathVariable Long medicalRecordId, @RequestBody AddRespiratoryRequest addRespiratoryRequest) {
//        return ResponseEntity.ok(respiratoryExamService.addRespiratory(medicalRecordId,addRespiratoryRequest));
//    }
//    @PutMapping("/cardiac/{cardiacId}")
//    public ResponseEntity<Boolean> updateNotes(@PathVariable Long cardiacId, @RequestBody AddRespiratoryRequest request) {
//        return ResponseEntity.ok(respiratoryExamService.updateRespiratory(respiratoryId,request));
//    }
//    @DeleteMapping("/cardiac/{cardiacId}")
//    public ResponseEntity<Boolean> deleteNotes(@PathVariable Long cardiacId) {
//        return ResponseEntity.ok(respiratoryExamService.deleteRespiratory(respiratoryId));
//    }
}
