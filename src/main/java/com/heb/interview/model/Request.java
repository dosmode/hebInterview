package com.heb.interview.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.heb.interview.model.DetectedObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {

    private UUID requestId;
    private String imageFilePath;
    private String label;
    private boolean enableObjectDetection;
    private List<DetectedObject> detectedObjectList;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date lastModifiedDate;
}
