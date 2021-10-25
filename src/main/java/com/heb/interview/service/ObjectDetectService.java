package com.heb.interview.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class ObjectDetectService {

    public static Map<String, Float> detectLocalizedObjects(InputStream filePath, boolean enableObjectDetection) throws IOException {
        if (!enableObjectDetection) return new HashMap<>();
        Map<String, Float> result = new HashMap<>();
        Logger logger = LoggerFactory.getLogger(ObjectDetectService.class);
        List<AnnotateImageRequest> requests = new ArrayList<>();
        ByteString imgBytes = ByteString.readFrom(filePath);

        Image img = Image.newBuilder().setContent(imgBytes).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder()
                        .addFeatures(Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION))
                        .setImage(img)
                        .build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            // Perform the request
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            // store the results
            for (AnnotateImageResponse res : responses) {
                for (LocalizedObjectAnnotation entity : res.getLocalizedObjectAnnotationsList()) {
                    result.put(entity.getName(), result.getOrDefault(entity.getName(), (float) (entity.getScore() * 100.0)) > (float) (entity.getScore() * 100.0) ? (result.getOrDefault(entity.getName(), (float) (entity.getScore() * 100.0))) : (float) (entity.getScore() * 100.0));
                    logger.info("Object name: {}", entity.getName());
                    logger.info("Confidence: {}%", Math.round(entity.getScore() * 100.00));
                }
            }

        }
        return result;
    }
}