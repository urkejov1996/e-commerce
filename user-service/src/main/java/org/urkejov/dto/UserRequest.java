package org.urkejov.dto;

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
    @NotNull
    private List<UserRoleEnum> roles;
    private String password;
}
