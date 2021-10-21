package com.heb.interview.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class requestDto {
    private String id;
    private String imageFilePath;
    private String label;
    private boolean enableObjectDetection;

    public Optional<String> getLabel(){
        return Optional.ofNullable(label);
    }
}