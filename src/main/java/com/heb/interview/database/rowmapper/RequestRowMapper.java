package com.heb.interview.database.rowmapper;

import com.heb.interview.model.Request;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RequestRowMapper implements RowMapper<Request> {
    public Request mapRow(ResultSet rs, int rowNum) throws SQLException {
        Request student = new Request();
        student.setRequestId(UUID.fromString(rs.getString("id")));
        student.setImageFilePath(rs.getString("ImageFilePath"));
        student.setEnableObjectDetection(rs.getBoolean("EnableObjectDetection"));
        student.setLabel(rs.getString("label"));
        return student;
    }
}