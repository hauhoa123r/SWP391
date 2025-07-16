package org.project.service.impl;

import org.project.converter.ReferenceRangeConverter;
import org.project.model.response.SymtomResponse;
import org.project.service.ReferrenceRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReferenceRangeServiceImpl implements ReferrenceRangeService {

    @Autowired
    private ReferenceRangeConverter referenceRangeConverter;

    @Override
    public SymtomResponse getSymtomResponse(Map<String, String> dataUnit) {
        SymtomResponse symtomResponse = referenceRangeConverter.getSymtomResponse(dataUnit);
        return symtomResponse;
    }
}
