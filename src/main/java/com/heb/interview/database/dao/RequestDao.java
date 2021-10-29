package com.heb.interview.database.dao;

import com.heb.interview.model.Request;
import com.heb.interview.database.rowmapper.RequestRowMapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component("RequestDao")
public class RequestDao extends AbstractDao {

    private static final String SQL_ADD_REQUEST =
            "INSERT INTO dbo.request (requestId,imageFilePath,label,enableObjectDetection) " +
                    "VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_REQUEST =
            "UPDATE dbo.request " +
                    "SET imageFilePath = :imageFilePath, " +
                    "label = :label, " +
                    "enableObjectDetection = :enableObjectDetection, " +
                    "lastModifiedDate = :lastModifiedDate " +
                    "WHERE requestId = :requestId";
    private static final String SQL_GET_ALL_REQUEST =
            "SELECT * FROM dbo.request";
    private static final String SQL_GET_REQUEST_BY_ID =
            "SELECT * FROM dbo.request WHERE requestId = ?";
    private static final String SQL_GET_REQUEST_BY_FILE_PATH =
            "SELECT * FROM dbo.request WHERE imageFilePath = ?";

    private static final String SQL_GET_ALL_REQUEST_BY_OBJECT_NAME =
            "SELECT R.requestId, R.imageFilePath, R.label, R.enableObjectDetection, R.lastModifiedDate " +
                    "FROM Request AS R " +
                    "JOIN detectedObject AS DO on DO.requestId = R.requestId " +
                    "WHERE DO.objectName IN (:objects)";

    public int insert(Request record) {

        if (record == null) {
            throw new IllegalArgumentException("record object cannot be null");
        }
        if (record.getImageFilePath() == null) {
            throw new IllegalArgumentException("record name cannot be null");
        }
        return getJdbcTemplate().update(SQL_ADD_REQUEST, record.getRequestId(), record.getImageFilePath(), record.getLabel(), record.isEnableObjectDetection());
    }

    public int update(Request record) {

        if (record == null) {
            throw new IllegalArgumentException("record object cannot be null");
        }
        if (record.getImageFilePath() == null) {
            throw new IllegalArgumentException("record name cannot be null");
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("requestId", record.getRequestId())
                .addValue("imageFilePath", record.getImageFilePath())
                .addValue("label", record.getLabel())
                .addValue("lastModifiedDate", record.getLastModifiedDate())
                .addValue("enableObjectDetection", record.isEnableObjectDetection());
        return this.getNamedParameterJdbcTemplate().update(SQL_UPDATE_REQUEST, params);
    }

    public Collection<Request> getTagGroupByTagTypeForPlant(UUID tagTypeId, UUID plantId) {
        return this.getNamedParameterJdbcTemplate().query(SQL_UPDATE_REQUEST,
                new MapSqlParameterSource()
                        .addValue("plantId", plantId)
                        .addValue("tagTypeId", tagTypeId)
                , new RequestRowMapper());
    }

    public List<Request> findByFileName(String record) {

        if (record == null) {
            throw new IllegalArgumentException("TagGroup object cannot be null");
        }

        return getJdbcTemplate().query(SQL_GET_REQUEST_BY_FILE_PATH, BeanPropertyRowMapper.newInstance(Request.class), record);
    }

    public List<Request> findAllRequest() {

        return getJdbcTemplate().query(SQL_GET_ALL_REQUEST, BeanPropertyRowMapper.newInstance(Request.class));

    }

    public List<Request> findAllByRequestObject(List<String> objectArray) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("objects", objectArray);
        return getNamedParameterJdbcTemplate().query(SQL_GET_ALL_REQUEST_BY_OBJECT_NAME, params, BeanPropertyRowMapper.newInstance(Request.class));
    }

    public Request findByImageId(String imageID) {

        if (imageID == null) {
            throw new IllegalArgumentException("TagGroup object cannot be null");
        }

        return getJdbcTemplate().query(SQL_GET_REQUEST_BY_ID, BeanPropertyRowMapper.newInstance(Request.class), imageID).get(0);
    }

}