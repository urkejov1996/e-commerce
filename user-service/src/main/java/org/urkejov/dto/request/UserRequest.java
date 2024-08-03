package org.urkejov.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.urkejov.tools.enums.UserRoleEnum;
import org.urkejov.tools.enums.UserStatusEnum;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String username;
    @NotNull
    private String email;
    private String phoneNumber;
    private String notes;
    @NotNull
    private UserStatusEnum status;
    @NotNull
    private List<UserRoleEnum> roles;
    @NotNull
    private String password;
}
