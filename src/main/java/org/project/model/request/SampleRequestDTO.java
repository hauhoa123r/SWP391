package org.project.model.request;

import lombok.Data;
import org.project.model.dto.SampleDTO;

import java.util.List;

@Data
public class SampleRequestDTO {
    private String sampleGroupId;
    private List<SampleDTO> samples;
}
