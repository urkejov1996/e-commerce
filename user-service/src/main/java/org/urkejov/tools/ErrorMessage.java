package org.urkejov.tools;

import lombok.Data;

@Data
public class ErrorMessage {
    public static final String BAD_REQUEST = "Unbelievably very bad request";
    public static final String ALREADY_EXIST = "Already exist.";
    public static final String NOT_FOUND = "Resource not found.";
    public static final String ACCESS_DENIED = "Access denied.";
    public static final String DELETE_CONSTRAINT = "Unable to delete the resource as it is currently being utilized in another part of the application.";
    public static final String DELETE_YOURSELF = "Unable to delete yourself.";
    public static final String INVALID_DATA = "Invalid data.";
}
