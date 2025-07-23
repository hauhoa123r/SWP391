package org.project.service;

import org.project.entity.ResultEntity;

import java.util.Map;

public interface ResultSampleService {
     ResultEntity isSaveResultSample(Map<String, String> dataUnit);
}
