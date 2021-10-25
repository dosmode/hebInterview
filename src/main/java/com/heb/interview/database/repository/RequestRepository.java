package com.heb.interview.database.repository;

import com.heb.interview.database.dao.RequestDao;
import com.heb.interview.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestRepository {

    @Autowired
    private transient RequestDao requestDao;

    public int add(Request entity) {
        return requestDao.insert(entity);
    }

    public int update(Request entity) {
        return requestDao.update(entity);
    }

    public List<Request> findByFileName(String entity) {
        return requestDao.findByFileName(entity);
    }

    public List<Request> findAllRequest() {
        return requestDao.findAllRequest();
    }

    public List<Request> findALLByDetectedObject(List<String> entity) {
        return requestDao.findAllByRequestObject(entity);
    }
}