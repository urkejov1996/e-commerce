package org.urkejov.service;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.urkejov.dto.request.UserRequest;
import org.urkejov.dto.response.UserResponse;
import org.urkejov.entity.User;
import org.urkejov.repository.UserRepository;
import org.urkejov.tools.ErrorMessage;
import org.urkejov.tools.enums.UserRoleEnum;
import org.urkejov.tools.enums.UserStatusEnum;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return ResponseEntity containing the user data or an error message
     */
    public ResponseEntity<?> getUser(String userId) {
        UserResponse userResponse = new UserResponse();
        try {
            if (userId != null && !userId.isEmpty()) {
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
            log.error("An error occurred while retrieving the user with ID: {}", userId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all users with optional pagination and sorting.
     *
     * @param size   the number of users per page
     * @param sortBy the attribute to sort by
     * @param page   the page number to retrieve
     * @return ResponseEntity containing the list of users or an error message
     */
    public ResponseEntity<?> getAllUsers(Optional<Integer> size, Optional<String> sortBy, Optional<Integer> page) {
        UserResponse userResponse = new UserResponse();
        try {
            Page<User> users = userRepository.findAll(
                    PageRequest.of(
                            page.orElse(0),
                            size.orElse(10),
                            Sort.Direction.DESC, sortBy.orElse("createdAt")
                    )
            );
            if (page.isPresent() && page.get() >= users.getTotalPages()) {
                userResponse.addError(ErrorMessage.NOT_FOUND);
                return new ResponseEntity<>(userResponse, HttpStatus.NOT_FOUND);
            }
            if (users.isEmpty()) {
                userResponse.addInfo("There are no users yet");
                userResponse.setData(new ArrayList<>());
                return new ResponseEntity<>(userResponse, HttpStatus.OK);
            }
            List<UserResponse> userResponses = users.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            userResponse.setData(userResponses);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred while retrieving all users: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new user based on the provided UserRequest data.
     *
     * @param bindingResult the result of the validation of the request
     * @param userRequest   the request object containing user details
     * @param jwt           the JWT token of the requesting user
     * @return ResponseEntity containing the created user data or an error message
     */
    public ResponseEntity<?> create(BindingResult bindingResult, UserRequest userRequest, Jwt jwt) {
        UserResponse userResponse = new UserResponse();
        if (bindingResult.hasErrors()) {
            UserResponse finalUserResponse = userResponse;
            bindingResult.getAllErrors().forEach(error -> {
                finalUserResponse.addError(error.getDefaultMessage());
            });
            return new ResponseEntity<>(userResponse, HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<User> optionalUser = userRepository.findUserByEmail(userRequest.getEmail());
            if (optionalUser.isPresent()) {
                userResponse.addError(ErrorMessage.ALREADY_EXIST);
                return new ResponseEntity<>(userResponse, HttpStatus.BAD_REQUEST);
            }
            UserRecord userFirebase = null;
            try {
                userFirebase = FirebaseAuth.getInstance().getUserByEmail(userRequest.getEmail());
            } catch (FirebaseAuthException e) {
                if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
                    UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                            .setEmail(userRequest.getEmail())
                            .setEmailVerified(true)
                            .setPassword(userRequest.getPassword())
                            .setDisplayName(userRequest.getUsername())
                            .setDisabled(userRequest.getStatus().equals(UserStatusEnum.INACTIVE));
                    userFirebase = FirebaseAuth.getInstance().createUser(request);
                } else {
                    throw new RuntimeException("Firebase Exception :: " + e.getMessage() + " :: code name :: " + e.getAuthErrorCode().name());
                }
            }
            List<String> roles = new ArrayList<>();
            if (userRequest.getRoles().get(0) == UserRoleEnum.ADMIN) {
                roles.add(userRequest.getRoles().get(0).name());
                roles.add(UserRoleEnum.USER.name());
            }
            if (userRequest.getRoles().get(0) == UserRoleEnum.USER) {
                roles.add(userRequest.getRoles().get(0).name());
            }

            FirebaseAuth.getInstance().setCustomUserClaims(userFirebase.getUid(), Map.of("roles", Collections.unmodifiableList(roles)));
            User user = User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .username(userRequest.getUsername())
                    .email(userRequest.getEmail())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .userStatus(userRequest.getStatus())
                    .userRole(userRequest.getRoles().get(0))
                    .firebaseUid(userFirebase.getUid())
                    .notes(userRequest.getNotes())
                    .build();

            userResponse = mapToDto(user);
            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("An error occurred while creating user with email {}", userRequest.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> update(String userId, UserRequest userRequest) {
        UserResponse userResponse = new UserResponse();
        try {
            if (userId==null || userId.isEmpty()){
                userResponse.addError(ErrorMessage.BAD_REQUEST);
                return new ResponseEntity<>(userResponse, HttpStatus.BAD_REQUEST);
            }
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                userResponse.addError(ErrorMessage.NOT_FOUND);
                return new ResponseEntity<>(userResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            log.error("An error occurred while updating user with email {}", userRequest.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Maps a User entity to a UserResponse DTO.
     *
     * @param user the user entity to map
     * @return the mapped UserResponse DTO
     */
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
