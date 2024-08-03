package org.urkejov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.urkejov.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<?> getUser(String userId) {




        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
