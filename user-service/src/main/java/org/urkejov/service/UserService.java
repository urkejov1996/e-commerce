package org.urkejov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.urkejov.dto.response.UserResponse;
import org.urkejov.entity.User;
import org.urkejov.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<?> getUser(String userId) {
        UserResponse userResponse = new UserResponse();
        try {
            if (userId != null) {
                Optional<User> optionalUser = userRepository.findById(userId);
                if (optionalUser.isEmpty()) {
                    userResponse.addError("User not found.");
                    return new ResponseEntity<>(userResponse, HttpStatus.NOT_FOUND);
                } else {
                    User user = optionalUser.get();
                    userResponse = mapToDto(user);
                    return new ResponseEntity<>(userResponse, HttpStatus.OK);
                }
            }
            userResponse.addError("Invalid user ID");
            return new ResponseEntity<>(userResponse.getErrors(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserResponse mapToDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .notes(user.getNotes())
                .firebaseUid(user.getFirebaseUid())
                .userStatus(user.getUserStatus())
                .userRole(user.getUserRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .lastLoginDate(user.getLastLoginDate())
                .build();
    }


}
