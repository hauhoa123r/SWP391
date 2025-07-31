package org.project.service;

import org.project.model.dto.*;

public interface ExaminationService {
    Boolean addVitalSign(VitalSignDTO vitalSignDTO);
    Boolean addRespiratory(RespiratoryDTO respiratoryDTO);
    Boolean addCardiacExam(CardiacDTO cardiacDTO);
    Boolean addNeurologic(NeurologicDTO neurologicDTO);
    Boolean addGastrointestinal(GastrointestinalDTO gastrointestinalDTO);
    Boolean addGenitourinary(GenitourinaryDTO genitourinaryDTO);
    Boolean addMusculoskeletal(MusculoskeletalDTO musculoskeletalDTO);
    Boolean addDermatologic(DermatologicDTO dermatologicDTO);
}
