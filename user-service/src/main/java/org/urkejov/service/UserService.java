package org.urkejov.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    public ResponseEntity<?> getUser(String userId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
