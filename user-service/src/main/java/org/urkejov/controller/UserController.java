package org.urkejov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.urkejov.service.UserService;
import org.urkejov.tools.RoleTools;
import org.urkejov.tools.enums.UserRoleEnum;

import java.util.ArrayList;
import java.util.List;

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
}
