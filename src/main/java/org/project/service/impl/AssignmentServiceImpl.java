package org.project.service.impl;

import org.project.converter.AssignmentListConverter;
import org.project.entity.TestRequestEntity;
import org.project.enums.RequestStatus;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.AssignmentListDTO;
import org.project.repository.AssignmentRepository;
import org.project.repository.impl.AssignmentRepositoryCustomImpl;
import org.project.service.AssignmentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepositoryCustomImpl assignmentRepositoryCustom;
    private final AssignmentListConverter assignmentListConverter;
    private final AssignmentRepository assignmentRepository;

    public AssignmentServiceImpl(AssignmentRepositoryCustomImpl assignmentRepositoryCustom,
                                 AssignmentListConverter assignmentListConverter, AssignmentRepository assignmentRepository) {
        this.assignmentRepositoryCustom = assignmentRepositoryCustom;
        this.assignmentListConverter = assignmentListConverter;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public Page<AssignmentListDTO> getAssignmentBySearch(AssignmentListDTO search) throws IllegalAccessException {
        Page<AssignmentListDTO> results = assignmentListConverter.getAllAssignmentBySearch(search);
        if (results == null || results.isEmpty()) {
            throw new ResourceNotFoundException("No resource found");
        }
        return results;
    }

    @Override
    public boolean receivePatient(Long id) {
        Optional<TestRequestEntity> optionalTestRequestEntity = assignmentRepository
                .findById(id);
        if (!optionalTestRequestEntity.isPresent()) {
            throw new ResourceNotFoundException("Assignment not found");
        }
        TestRequestEntity testRequestEntity = optionalTestRequestEntity.get();
        testRequestEntity.setRequestStatus(RequestStatus.received);
        assignmentRepository.save(testRequestEntity);
        return true;
    }

    @Override
    public boolean reveicePatientByMultileChoise(List<Long> ids) {
        for(Long id : ids){
            TestRequestEntity testRequestEntity = assignmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Receive false with id" + id));
            testRequestEntity.setRequestStatus(RequestStatus.received);
            assignmentRepository.save(testRequestEntity);
        }
        return true;
    }

    @Override
    public Page<AssignmentListDTO> getReceivedPatientBySearch(AssignmentListDTO search) throws IllegalAccessException {
        Page<AssignmentListDTO> results = assignmentListConverter.getAllReceivePatient(search);
        if (results == null || results.isEmpty()) {
            throw new ResourceNotFoundException("No resource found");
        }
        return results;
    }
}
