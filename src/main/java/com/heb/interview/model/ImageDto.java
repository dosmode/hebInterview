package com.heb.interview.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
public class ImageDto {
    private String id;
    private String imageFilePath;
    private String label;
    private Boolean enableObjectDetection;

    public Optional<String> getLabel(){
        return Optional.ofNullable(label);
    }
}