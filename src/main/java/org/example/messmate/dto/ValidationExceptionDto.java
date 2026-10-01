package org.example.messmate.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ValidationExceptionDto extends ApiExceptionDto {
    private Map<String, String> fieldError;

    public ValidationExceptionDto(int status, String error, String message, String path, Map<String, String> fieldError) {
        super(status, error, message, path);
        this.fieldError = fieldError;
    }

}
