package com.heb.interview.utill;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidValidator {
    public boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
