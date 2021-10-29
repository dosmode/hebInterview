package com.heb.interview.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.interview.model.Request;
import com.heb.interview.database.repository.DetectedObjectRepository;
import com.heb.interview.database.repository.RequestRepository;
import com.heb.interview.service.ImageHandlingService;
import com.heb.interview.service.ObjectDetectService;
import com.heb.interview.utill.UuidValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class ImageController {

    @Autowired
    UuidValidator uuidValidator;
    ObjectDetectService objectDetectService;
    final ImageHandlingService imageHandlingService;

    private final RequestRepository requestRepository;
    private final DetectedObjectRepository detectedObjectRepository;

    final Logger logger = LoggerFactory.getLogger(ImageController.class);
    final String IMAGE_OBJECT_QUERY_REGEX = "^\"+([\\w, &])+\"$";
    final Pattern IMAGE_OBJECT_QUERY_PATTERN = Pattern.compile(IMAGE_OBJECT_QUERY_REGEX);

    private final static String PROJECT_IMAGE_STORE_PATH = "src/main/resources/imageSources/";

    @Autowired
    public ImageController(ImageHandlingService imageHandlingService, RequestRepository requestRepository, DetectedObjectRepository detectedObjectRepository) {
        this.imageHandlingService = imageHandlingService;
        this.requestRepository = requestRepository;
        this.detectedObjectRepository = detectedObjectRepository;
    }


    @RequestMapping(value = "/images", method = RequestMethod.GET)
    public ResponseEntity<Object> getImageUsingQueryParam(@RequestParam(required = false, name = "objects") String object) {
        if (!StringUtils.isEmpty(object)) {
            Matcher objectQueryMatcher = IMAGE_OBJECT_QUERY_PATTERN.matcher(object);
            logger.info("/images call with optional query param has been triggered.");
            if (objectQueryMatcher.find()) {
                List<String> objectList = Arrays.asList(objectQueryMatcher.group(0).replaceAll("\"", "").split(","));
                List<Request> queriedRequest = requestRepository.findALLByDetectedObject(objectList);
                queriedRequest.forEach(q -> q.setDetectedObjectList(detectedObjectRepository.fineAllByRequestID(q.getRequestId())));
                return new ResponseEntity<>(queriedRequest, HttpStatus.OK);

            } else {
                String error = "query pattern [[" + object + "]] doesn't allow. Please check the query param. ex) \"dog,cat\". your input query param is";
                logger.error(error + " {} ", object);
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        } else {
            List<Request> requestObject = requestRepository.findAllRequest();
            requestObject.forEach(
                    request -> request.setDetectedObjectList(
                            detectedObjectRepository.findALLByDetectedObjectId(
                                    request.getRequestId().toString()
                            )
                    )
            );
            logger.info("/images call has been triggered. All image metadata will present");
            return new ResponseEntity<>(requestObject, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/images/{imageId}", method = RequestMethod.GET)
    public ResponseEntity<Object>  getImageUsingPathParam(@PathVariable String imageId) {
        if (!uuidValidator.isUUID(imageId)) {
            logger.error("Invalid UUID: {} detected. Please enter valid UUID.", imageId);
        } else {
            logger.info("/images call with path param has been triggered. UUID: {}", imageId);
        }
        Request requestObject = requestRepository.findByImageID(imageId);
        requestObject.setDetectedObjectList( detectedObjectRepository.findALLByDetectedObjectId( requestObject.getRequestId().toString() ));
        return new ResponseEntity<>(requestObject, HttpStatus.OK);
    }


    @Transactional
    @RequestMapping(value = "/images", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFile(@RequestParam(required = false, value = "imageFile") MultipartFile imageFile,
                                             @RequestParam(value = "jsonData") String jsonData) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Request userData = objectMapper.readValue(jsonData, Request.class);
        Date date = new Date();
        userData.setLastModifiedDate(new java.sql.Date(date.getTime()));
        InputStream input = null;
        String filePath = null;
        if (imageFile != null) {
            filePath = PROJECT_IMAGE_STORE_PATH + imageFile.getOriginalFilename();
            input = saveImageToLocal(imageFile);
        } else {
            try {
                filePath = userData.getImageFilePath();
                input = new URL(filePath).openStream();
            } catch (MalformedURLException e) {
                logger.error("Invalid URL");
            } catch (Exception e) {
                logger.error("Couldn't process with provided url");
            }
        }

        userData = imageHandlingService.objectDetectionService(userData, input, filePath);

        return new ResponseEntity<>(userData, HttpStatus.OK);
    }


    private InputStream saveImageToLocal(MultipartFile imageFile) {
        byte[] bytes;
        InputStream targetStream = null;
        try {
            bytes = imageFile.getBytes();
            targetStream = new ByteArrayInputStream(bytes);

            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(PROJECT_IMAGE_STORE_PATH + imageFile.getOriginalFilename()));
            stream.write(bytes);
            stream.close();
            logger.info("Successfully stored uploaded file in local");
        } catch (Exception e) {
            logger.error("Failed to store the file in local");
        }
        return targetStream;
    }

//
//    @DeleteMapping(path = "/{imageId}")
//    public Request delete(@PathVariable("imageId") int imageId) {
//
//        return null;
//    }


}