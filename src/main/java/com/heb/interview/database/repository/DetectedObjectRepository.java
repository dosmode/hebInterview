package com.heb.interview.database.repository;

import com.heb.interview.database.dao.DetectedObjectDao;
import com.heb.interview.model.DetectedObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DetectedObjectRepository {

    @Autowired
    private transient DetectedObjectDao detectedObjectDao;

    public int add(DetectedObject entity) {
        return detectedObjectDao.insert(entity);
    }

    public int[] batchInsert(List<DetectedObject> entity) {
        return detectedObjectDao.batchInsert(entity);
    }

    public int update(DetectedObject entity) {
        return detectedObjectDao.update(entity);
    }

    public List<DetectedObject> findALLByDetectedObjectId(String entity) {
        return detectedObjectDao.findAllByRequestID(entity);
    }

    public int deleteAllByRequestID(UUID requestID) {
        return detectedObjectDao.deleteALLByRequestID(requestID);
    }

    public List<DetectedObject> fineAllByRequestID(UUID requestID) {
        return detectedObjectDao.fineAllByRequestID(requestID);
    }

}