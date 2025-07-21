package org.project.service;

import org.project.model.response.SymtomResponse;

import java.util.Map;

public interface ReferrenceRangeService {
    SymtomResponse getSymtomResponse(Map<String, String> dataUnit);
}
