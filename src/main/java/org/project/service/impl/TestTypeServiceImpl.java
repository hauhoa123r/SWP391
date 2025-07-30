//package org.project.service.impl;
//
//import org.project.converter.TestTypeConverter;
//import org.project.entity.ReferenceRangeEntity;
//import org.project.entity.TestItemEntity;
//import org.project.entity.TestTypeEntity;
//import org.project.exception.ResourceNotFoundException;
//import org.project.model.request.SampleRequestDTO;
//import org.project.model.response.TestTypeListResponse;
//import org.project.repository.ReferenceRangeRepository;
//import org.project.repository.TestItemRepository;
//import org.project.repository.TestTypeRepository;
//import org.project.service.TestTypeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Transactional
//public class TestTypeServiceImpl implements TestTypeService {
//
//    @Autowired
//    private TestTypeRepository testTypeRepository;
//
//    @Autowired
//    private TestTypeConverter testTypeConverter;
//
//    @Autowired
//    private TestItemRepository testItemRepository;
//
//    @Autowired
//    private ReferenceRangeRepository referenceRangeRepository;
//    @Override
//    public Page<TestTypeListResponse> searchTestTypes(String keyword, Pageable pageable) {
//        Page<TestTypeEntity> typeEntities = testTypeRepository.findByTestTypeNameContainingIgnoreCase(keyword, pageable);
//
//        return typeEntities.map(testTypeEntity -> testTypeConverter.toTestTypeListResponse(testTypeEntity));
//    }
//    @Override
//    public Page<TestTypeListResponse> getAllTestTypes(Pageable pageable) {
//        Page<TestTypeEntity> typeEntities = testTypeRepository.findAll(pageable);
//        return typeEntities.map(testTypeEntity -> testTypeConverter.toTestTypeListResponse(testTypeEntity));
//    }
//
//    @Override
//    public Boolean isCreateTestType(SampleRequestDTO sampleRequestDTO) {
//        TestTypeEntity testTypeEntity = new TestTypeEntity();
//        testTypeEntity.setTestTypeName(sampleRequestDTO.getSampleGroupId());
//        testTypeEntity.setProduct(null);
//        testTypeEntity.setStatus("active");
//        testTypeRepository.save(testTypeEntity);
//
//        sampleRequestDTO.getSamples().forEach(sample -> {
//            TestItemEntity testItemEntity = new TestItemEntity();
//            testItemEntity.setName(sample.getName());
//            testItemEntity.setUnit(sample.getUnit());
//            try {
//                testItemEntity.setRefMin(new BigDecimal(sample.getLow()));
//                testItemEntity.setRefMax(new BigDecimal(sample.getHigh()));
//            } catch (NumberFormatException e) {
//                throw new RuntimeException("Dữ liệu số không hợp lệ ở mẫu: " + sample.getName(), e);
//            }
//
//            testItemEntity.setTestTypeEntity(testTypeEntity);
//            testItemRepository.save(testItemEntity);
//
//            ReferenceRangeEntity referenceRangeEntity = new ReferenceRangeEntity();
//            referenceRangeEntity.setAgeMin(0);
//            referenceRangeEntity.setAgeMax(100);
//            referenceRangeEntity.setGender("Male");
//            referenceRangeEntity.setMinValue(new BigDecimal(sample.getLow()));
//            referenceRangeEntity.setMaxValue(new BigDecimal(sample.getHigh()));
//            referenceRangeEntity.setMinCondition(sample.getLowSuspect());
//            referenceRangeEntity.setMaxCondition(sample.getHighSuspect());
//            referenceRangeEntity.setTestItemEntity(testItemEntity);
//            referenceRangeRepository.save(referenceRangeEntity);
//        });
//
//        return true;
//    }
//
//    @Override
//    public Boolean isDeleteTestType(Long id) {
//        Optional<TestTypeEntity> testTypeEntity = testTypeRepository.findById(id);
//        if(!testTypeEntity.isPresent()){
//            throw new ResourceNotFoundException("not found test type with id: " + id);
//        }
//
//        testTypeEntity.get().setStatus("inactive");
//        testTypeRepository.save(testTypeEntity.get());
//        return true;
//    }
//
//    @Override
//    public Boolean isRestoreTestType(Long id) {
//        Optional<TestTypeEntity> testTypeEntity = testTypeRepository.findById(id);
//        if(!testTypeEntity.isPresent()){
//            throw new ResourceNotFoundException("not found test type with id: " + id);
//        }
//
//        testTypeEntity.get().setStatus("active");
//        testTypeRepository.save(testTypeEntity.get());
//        return true;
//    }
//}
