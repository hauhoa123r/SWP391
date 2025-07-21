package org.project.service;

import org.project.model.request.AddCardiac;
import org.project.model.response.CardiacResponse;
import org.project.model.response.RespiratoryResponse;

import java.util.List;

public interface CardiacExamService {
    boolean addCardiac(Long medicalRecordId, AddCardiac addCardiac);
    boolean updateCardiac(Long cardiacId, AddCardiac addCardiac);
    boolean deleteCardiac(Long cardiacId);
    List<CardiacResponse> getCardiac(Long medicalRecordId);
}
