package org.project.api;

import org.project.model.request.AddCardiac;
import org.project.model.request.AddNeurologicRequest;
import org.project.model.request.AddRespiratoryRequest;
import org.project.model.request.AddVitalSignRequest;
import org.project.model.response.CardiacResponse;
import org.project.model.response.NeurologicResponse;
import org.project.model.response.RespiratoryResponse;
import org.project.model.response.VitalSignResponse;
import org.project.service.CardiacExamService;
import org.project.service.RespiratoryExamService;
import org.project.service.VitalSignService;
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

    //Neurologic
    @GetMapping("/neurologic/{medicalRecordId}")
    public ResponseEntity<List<NeurologicResponse>> getNeurologic(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(respiratoryExamService.getRespiratory(medicalRecordId));
    }
    @PostMapping("/neurologic/{medicalRecordId}/add")
    public ResponseEntity<Boolean> addNeurologic(@PathVariable Long medicalRecordId, @RequestBody AddNeurologicRequest addNeurologicRequest) {
        return ResponseEntity.ok(respiratoryExamService.addRespiratory(medicalRecordId,addNeurologicRequest));
    }
    @PutMapping("/neurologic/{neurologicId}")
    public ResponseEntity<Boolean> updateNeurologic(@PathVariable Long neurologicId, @RequestBody AddNeurologicRequest addNeurologicRequest) {
        return ResponseEntity.ok(respiratoryExamService.updateRespiratory(respiratoryId,addNeurologicRequest));
    }
    @DeleteMapping("/neurologic/{neurologicId}")
    public ResponseEntity<Boolean> deleteNeurologic(@PathVariable Long neurologicId) {
        return ResponseEntity.ok(respiratoryExamService.deleteRespiratory(respiratoryId));
    }
//    //Gastro
//    @GetMapping("/cardiac/{medicalRecordId}")
//    public ResponseEntity<List<RespiratoryResponse>> getGastro(@PathVariable Long medicalRecordId) {
//        return ResponseEntity.ok(respiratoryExamService.getRespiratory(medicalRecordId));
//    }
//    @PostMapping("/cardiac/{medicalRecordId}/add")
//    public ResponseEntity<Boolean> addGastro(@PathVariable Long medicalRecordId, @RequestBody AddRespiratoryRequest addRespiratoryRequest) {
//        return ResponseEntity.ok(respiratoryExamService.addRespiratory(medicalRecordId,addRespiratoryRequest));
//    }
//    @PutMapping("/cardiac/{cardiacId}")
//    public ResponseEntity<Boolean> updateGastro(@PathVariable Long cardiacId, @RequestBody AddRespiratoryRequest request) {
//        return ResponseEntity.ok(respiratoryExamService.updateRespiratory(respiratoryId,request));
//    }
//    @DeleteMapping("/cardiac/{cardiacId}")
//    public ResponseEntity<Boolean> deleteGastro(@PathVariable Long cardiacId) {
//        return ResponseEntity.ok(respiratoryExamService.deleteRespiratory(respiratoryId));
//    }
//    //Genitourinary
//    @GetMapping("/cardiac/{medicalRecordId}")
//    public ResponseEntity<List<RespiratoryResponse>> getGenitourinary(@PathVariable Long medicalRecordId) {
//        return ResponseEntity.ok(respiratoryExamService.getRespiratory(medicalRecordId));
//    }
//    @PostMapping("/cardiac/{medicalRecordId}/add")
//    public ResponseEntity<Boolean> addGenitourinary(@PathVariable Long medicalRecordId, @RequestBody AddRespiratoryRequest addRespiratoryRequest) {
//        return ResponseEntity.ok(respiratoryExamService.addRespiratory(medicalRecordId,addRespiratoryRequest));
//    }
//    @PutMapping("/cardiac/{cardiacId}")
//    public ResponseEntity<Boolean> updateGenitourinary(@PathVariable Long cardiacId, @RequestBody AddRespiratoryRequest request) {
//        return ResponseEntity.ok(respiratoryExamService.updateRespiratory(respiratoryId,request));
//    }
//    @DeleteMapping("/cardiac/{cardiacId}")
//    public ResponseEntity<Boolean> deleteGenitourinary(@PathVariable Long cardiacId) {
//        return ResponseEntity.ok(respiratoryExamService.deleteRespiratory(respiratoryId));
//    }
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
