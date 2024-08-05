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
}
