package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.ClinicalNoteEntity;
import org.project.model.response.ClinalNoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClinicalNoteConverter {
    @Autowired
    private ModelMapper modelMapper;

    public ClinalNoteResponse toClinalNoteResponse(ClinicalNoteEntity clinicalNoteEntity) {
        return modelMapper.map(clinicalNoteEntity, ClinalNoteResponse.class);
    }
}
