package ca.reppy.reppy_backend.dtos;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private String error;
    private Object trace;
}
