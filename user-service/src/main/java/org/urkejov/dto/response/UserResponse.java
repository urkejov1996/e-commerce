package org.urkejov.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.urkejov.tools.enums.UserRoleEnum;
import org.urkejov.tools.enums.UserStatusEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private String notes;
    private String firebaseUid;
    private UserStatusEnum userStatus;
    private UserRoleEnum userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime lastLoginDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> info = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> errors = new ArrayList<>();

    public void addInfo(String info) {
        if (this.info == null) {
            this.info = new ArrayList<>();
        }
        this.info.add(info);
    }

    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
    }
}
