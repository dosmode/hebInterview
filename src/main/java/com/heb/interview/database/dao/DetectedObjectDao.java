package com.heb.interview.database.dao;

import com.heb.interview.model.DetectedObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Component("DetectedObjectDao")
public class DetectedObjectDao extends AbstractDao {


    private static final String SQL_ADD_DETECTED_OBJECT =
            "INSERT INTO dbo.detectedObject (objectDetectionID,requestId,objectName,confidence) " +
                    "VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_DETECTED_OBJECT =
            "UPDATE dbo.detectedObject " +
                    "SET confidence = :confidence, " +
                    "requestId = :requestId, " +
                    "objectName = :objectName " +
                    "WHERE objectDetectionID = :objectDetectionID";

    private static final String SQL_GET_DETECTED_OBJECT_BY_ID =
            "SELECT * FROM dbo.detectedObject WHERE objectDetectionID = ?";

    private static final String SQL_GET_ALL_DETECTED_OBJECT_BY_REQUEST_ID =
            "SELECT * FROM dbo.detectedObject WHERE requestId = ?";

    private static final String SQL_DELETE_ALL_DETECTED_OBJECT_BY_REQUEST_ID =
            "DELETE FROM dbo.detectedObject WHERE requestId = ?";

    public int insert(DetectedObject record) {

        if (record == null) {
            throw new IllegalArgumentException("Object Detection object cannot be null");
        }
        if (record.getObjectDetectionID() == null) {
            throw new IllegalArgumentException("Object Detection ID cannot be null");
        }
        return getJdbcTemplate().update(SQL_ADD_DETECTED_OBJECT, record.getObjectDetectionID(), record.getRequestId(), record.getObjectName(), record.getConfidence());
    }

    public int[] batchInsert(List<DetectedObject> recordList) {
        if (recordList == null || recordList.isEmpty()) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Detroit"));
        Timestamp time = Timestamp.valueOf(now);
        return getJdbcTemplate().batchUpdate(SQL_ADD_DETECTED_OBJECT, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                DetectedObject detectedObject = recordList.get(i);
                ps.setString(1, detectedObject.getObjectDetectionID().toString());
                ps.setString(2, detectedObject.getRequestId().toString());
                ps.setString(3, detectedObject.getObjectName());
                ps.setFloat(4, detectedObject.getConfidence());
//                ps.setTimestamp(3,time);
            }

            @Override
            public int getBatchSize() {
                return recordList.size();
            }
        });
    }

    public int update(DetectedObject record) {

        if (record == null) {
            throw new IllegalArgumentException("Object Detection object cannot be null");
        }
        if (record.getObjectDetectionID() == null) {
            throw new IllegalArgumentException("Object Detection ID cannot be null");
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("objectDetectionID", record.getRequestId())
                .addValue("requestId", record.getRequestId())
                .addValue("objectName", record.getObjectName())
                .addValue("confidence", record.getConfidence());

        return this.getNamedParameterJdbcTemplate().update(SQL_UPDATE_DETECTED_OBJECT, params);
    }

    public List<DetectedObject> findAllByRequestID(String record) {

        if (record == null) {
            throw new IllegalArgumentException("TagGroup object cannot be null");
        }

        return getJdbcTemplate().query(SQL_GET_ALL_DETECTED_OBJECT_BY_REQUEST_ID, BeanPropertyRowMapper.newInstance(DetectedObject.class), record);
    }

    public int deleteALLByRequestID(UUID requestId) {

        return getJdbcTemplate().update(SQL_DELETE_ALL_DETECTED_OBJECT_BY_REQUEST_ID, requestId.toString());
    }


    public List<DetectedObject> fineAllByRequestID(UUID requestId) {

        return getJdbcTemplate().query(SQL_GET_ALL_DETECTED_OBJECT_BY_REQUEST_ID, BeanPropertyRowMapper.newInstance(DetectedObject.class), requestId.toString());
    }


//    private final RowMapper<Request> objectRequestRowMapper = (rs, rn) -> {
//
//        return Request.builder()
//                .requestId(UUID.fromString(rs.getString("requestId")))
//                .imageFilePath(rs.getString("imageFilePath"))
//                .label(rs.getString("label"))
//                .enableObjectDetection(rs.getBoolean("enableObjectDetection"))
//                .build();
//    };
}
