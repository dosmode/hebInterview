package com.heb.interview.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.interview.model.requestDto;
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

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class ImageController  {

    UuidValidator uuidValidator;
    ObjectDetectService objectDetectService;

    Logger logger = LoggerFactory.getLogger(ImageController.class);
    String IMAGE_OBJECT_QUERY_REGEX = "^\\\"+[\\w,]+\\\"$";
    Pattern IMAGE_OBJECT_QUERY_PATTERN = Pattern.compile(IMAGE_OBJECT_QUERY_REGEX);

    private final static String PROJECT_IMAGE_STORE_PATH = "src/main/resources/imageSources/";

    @Autowired
    public ImageController(UuidValidator uuidValidator, ObjectDetectService objectDetectService) {
        this.uuidValidator = uuidValidator;
        this.objectDetectService = objectDetectService;
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


    @RequestMapping(value="/images", method=RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFile(@RequestParam(required=false, value="imageFile") MultipartFile imageFile,
                                             @RequestParam(required=true, value="jsonData")String jsonData) throws IOException {
        requestDto result = new requestDto();
        ObjectMapper objectMapper = new ObjectMapper();
        requestDto userData = objectMapper.readValue(jsonData, requestDto.class);

        if (imageFile != null) {
            saveImageToLocal(imageFile);
            objectDetectService.detectLocalizedObjects(imageFile.getInputStream());
            return new ResponseEntity<>(jsonData, HttpStatus.OK);
        } else {
            InputStream input = null;
            try{
                input = new URL(userData.getImageFilePath()).openStream();
            }catch (MalformedURLException e){
                logger.error("Invalid URL");
            }catch (Exception e){
                logger.error("Couldn't process with provided url");
            }
            if(input != null){
                //TODO get returned ID from DB
                objectDetectService.detectLocalizedObjects(input);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void saveImageToLocal(MultipartFile imageFile) {
        try{
            byte[] bytes = imageFile.getBytes();
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(PROJECT_IMAGE_STORE_PATH + imageFile.getOriginalFilename()));
            stream.write(bytes);
            stream.close();
            logger.info("Successfully stored uploaded file in local");
        }catch (Exception e){
            logger.error("Failed to store the file in local");
        }
    }


    @DeleteMapping(path = "/{imageId}")
    public requestDto delete(@PathVariable("imageId") int imageId) {
        requestDto deletedEmp = null;

        return null;
    }


}
