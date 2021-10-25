package com.heb.interview.service;

import com.heb.interview.model.Request;
import com.heb.interview.model.DetectedObject;
import com.heb.interview.database.repository.DetectedObjectRepository;
import com.heb.interview.database.repository.RequestRepository;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class ImageHandlingService {
    private final RequestRepository requestRepository;
    private final DetectedObjectRepository detectedObjectRepository;
    ObjectDetectService objectDetectService;
    final Logger logger = LoggerFactory.getLogger(ImageHandlingService.class);

    @Autowired
    public ImageHandlingService(RequestRepository requestRepository, DetectedObjectRepository detectedObjectRepository) {
        this.requestRepository = requestRepository;
        this.detectedObjectRepository = detectedObjectRepository;
    }

    @Transactional
    public @NotNull Request objectDetectionService(Request userData, InputStream input, String filePath) {
        Map<String, Float> objectDetectedResult;
        Date date = new Date();
        if (input != null) {
            try {
                List<Request> dbRecord = requestRepository.findByFileName(filePath);
                objectDetectedResult = ObjectDetectService.detectLocalizedObjects(input, userData.isEnableObjectDetection());
                if (dbRecord.size() != 0) {
                    Request foundRecord = dbRecord.get(0);
                    userData.setRequestId(foundRecord.getRequestId());
                    userData.setImageFilePath(filePath);
                    userData.setLabel(StringUtils.isEmpty(userData.getLabel()) ? "default" : userData.getLabel());
                    userData.setLastModifiedDate(new java.sql.Timestamp(date.getTime()));
                    requestRepository.update(userData);
                    List<DetectedObject> objectLinkedList = new LinkedList<>();
                    objectLinkedListBuilder(objectDetectedResult, foundRecord.getRequestId(), objectLinkedList);
                    userData.setDetectedObjectList(objectLinkedList);
                    detectedObjectRepository.deleteAllByRequestID(foundRecord.getRequestId());
                    detectedObjectRepository.batchInsert(objectLinkedList);
                    logger.info("Successfully added record to DB {}", userData);

                } else {
                    final UUID requestUUID = UUID.randomUUID();
                    userData = Request.builder()
                            .requestId(requestUUID)
                            .imageFilePath(filePath)
                            .label(StringUtils.isEmpty(userData.getLabel()) ? "default" : userData.getLabel())
                            .enableObjectDetection(userData.isEnableObjectDetection())
                            .lastModifiedDate(userData.getLastModifiedDate())
                            .build();
                    List<DetectedObject> objectLinkedList = new LinkedList<>();
                    objectLinkedListBuilder(objectDetectedResult, requestUUID, objectLinkedList);
                    userData.setDetectedObjectList(objectLinkedList);
                    requestRepository.add(userData);
                    detectedObjectRepository.batchInsert(objectLinkedList);
                    logger.info("Successfully added record to DB {}", userData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return userData;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void objectLinkedListBuilder(Map<String, Float> objectDetectedResult, UUID requestUUID, List<DetectedObject> objectLinkedList) {
        Date date = new Date();
        objectDetectedResult.forEach((key, value) ->
                objectLinkedList.add(
                        DetectedObject.builder()
                                .objectDetectionID(UUID.randomUUID())
                                .objectName(key)
                                .confidence(value)
                                .requestId(requestUUID)
                                .lastModifiedDate(new java.sql.Date(date.getTime()))
                                .build()
                )
        );
    }

    private List<Request> getAllImages() {

        return requestRepository.findAllRequest();
    }
}