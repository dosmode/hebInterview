package com.heb.interview.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "detectedObject")
@Cacheable
public class DetectedObject {
    @Id
    @Basic(optional = false)
    private UUID objectDetectionID;

    private UUID requestId;
    private String objectName;
    private float confidence;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date lastModifiedDate;
}