package org.urkejov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.urkejov.dto.request.UserRequest;
import org.urkejov.service.UserService;
import org.urkejov.tools.RoleTools;
import org.urkejov.tools.enums.UserRoleEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * Fetch a user by their unique ID, only if the authenticated user has the appropriate role.
     *
     * @param userId The ID of the user to be fetched.
     * @param jwt    The JWT token containing user authentication details.
     * @return ResponseEntity containing the user data or an error message if unauthorized.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId, @AuthenticationPrincipal Jwt jwt) {
        if (RoleTools.hasAccess(jwt, new ArrayList<>(List.of(
                UserRoleEnum.ADMIN.name(),
                UserRoleEnum.USER.name()
        )))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return userService.getUser(userId);
    }

    /**
     * Fetch all users, with optional pagination and sorting, only if the authenticated user has the appropriate role.
     *
     * @param size  The number of users to fetch per page (optional).
     * @param sortBy The field to sort the results by (optional).
     * @param page  The page number to fetch (optional).
     * @param jwt   The JWT token containing user authentication details.
     * @return ResponseEntity containing a list of users or an error message if unauthorized.
     */
    @GetMapping()
    public ResponseEntity<?> getAllUsers(
            @RequestParam Optional<Integer> size,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<Integer> page,
            @AuthenticationPrincipal Jwt jwt) {
        if (RoleTools.hasAccess(jwt, new ArrayList<>(List.of(
                UserRoleEnum.ADMIN.name(),
                UserRoleEnum.USER.name()
        )))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return userService.getAllUsers(size, sortBy, page);
    }

    /**
     * Create a new user, only if the authenticated user has the appropriate role.
     *
     * @param userRequest   The data for the new user.
     * @param bindingResult Binding result for validation errors.
     * @param jwt           The JWT token containing user authentication details.
     * @return ResponseEntity containing the created user data or an error message if unauthorized.
     */
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody UserRequest userRequest, BindingResult bindingResult, @AuthenticationPrincipal Jwt jwt) {
        if (RoleTools.hasAccess(jwt, new ArrayList<>(List.of(
                UserRoleEnum.ADMIN.name(),
                UserRoleEnum.USER.name()
        )))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return userService.create(bindingResult, userRequest, jwt);
    }

    /**
     * Update an existing user by their unique ID, only if the authenticated user has the appropriate role.
     *
     * @param userId      The ID of the user to be updated.
     * @param userRequest The new data for the user.
     * @param jwt         The JWT token containing user authentication details.
     * @return ResponseEntity containing the updated user data or an error message if unauthorized.
     */
    @PutMapping("{userId}")
    public ResponseEntity<?> update(@PathVariable String userId, @RequestBody UserRequest userRequest, @AuthenticationPrincipal Jwt jwt) {
        if (RoleTools.hasAccess(jwt, new ArrayList<>(List.of(
                UserRoleEnum.ADMIN.name(),
                UserRoleEnum.USER.name()
        )))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return userService.update(userId, userRequest);
    }
}
