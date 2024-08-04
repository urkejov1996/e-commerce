package org.urkejov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.urkejov.dto.response.UserResponse;
import org.urkejov.entity.User;
import org.urkejov.repository.UserRepository;
import org.urkejov.tools.ErrorMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

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
