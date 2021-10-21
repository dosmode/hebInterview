package com.heb.interview.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.heb.interview.model.DetectedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ObjectDetectService {

    public static DetectedObject detectLocalizedObjects(InputStream filePath) throws IOException {
        Logger logger = LoggerFactory.getLogger(ObjectDetectService.class);
        DetectedObject detectedObject = null;
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

            // Display the results
            for (AnnotateImageResponse res : responses) {
                for (LocalizedObjectAnnotation entity : res.getLocalizedObjectAnnotationsList()) {
                    detectedObject = DetectedObject.builder().name(entity.getName())
                            .confidence((int) Math.round(entity.getScore() * 100.0)).build();
                    logger.info("Object name: {}", entity.getName());
                    logger.info("Confidence: {}%", Math.round(entity.getScore() * 100.0) );
                }
            }
        }
        return detectedObject;
    }
}
