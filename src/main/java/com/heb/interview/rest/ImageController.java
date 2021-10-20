package com.heb.interview.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.interview.model.ImageDto;
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

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class ImageController {

    UuidValidator uuidValidator;
    ObjectDetectService objectDetectService;

    Logger logger = LoggerFactory.getLogger(ImageController.class);
    String IMAGE_OBJECT_QUERY_REGEX = "^\\\"+[\\w,]+\\\"$";
    Pattern IMAGE_OBJECT_QUERY_PATTERN = Pattern.compile(IMAGE_OBJECT_QUERY_REGEX);


    @Autowired
    public ImageController(UuidValidator uuidValidator, ObjectDetectService objectDetectService) {
        this.uuidValidator = uuidValidator;
        this.objectDetectService = objectDetectService;
    }

    public ImageController(UuidValidator uuidValidator) {
        this.uuidValidator = uuidValidator;
    }


    @RequestMapping(value = "/images", method = RequestMethod.GET)
    public String getImageUsingQueryParam(@RequestParam(required = false, name = "objects") String object) {
        Matcher objectQueryMatcher = IMAGE_OBJECT_QUERY_PATTERN.matcher(object);
        if (!StringUtils.isEmpty(object)) {
            logger.info("/images call with optional query param has been triggered.");
            if (objectQueryMatcher.find()) {
                logger.info("requested object : {} ", object);
            } else {
                logger.error("query pattern doesn't allow. Please check the query param. ex) \"dog,cat\". your input query param is {} ", object);
            }
        } else {
            logger.info("/images call has been triggered. All image metadata will present");

        }
        return object;
    }

    @RequestMapping(value = "/images/{imageId}", method = RequestMethod.GET)
    public String getImageUsingPathParam(@PathVariable String imageId) {
        logger.info(UUID.randomUUID().toString());

        if (!uuidValidator.isUUID(imageId)) {
            logger.error("Invalid UUID: {} detected. Please enter valid UUID.", imageId);
        } else {
            logger.info("/images call with path param has been triggered. UUID: {}", imageId);

        }

        return imageId.toString();
    }

    @DeleteMapping(path = "/{imageId}")
    public ImageDto delete(@PathVariable("imageId") int imageId) {
        ImageDto deletedEmp = null;

        return null;
    }


    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadFile(@RequestBody ImageDto imageDto) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

            objectDetectService.detectLocalizedObjects(imageDto.getImageFilePath());


        return new ResponseEntity<>(imageDto, HttpStatus.OK);
    }

}
