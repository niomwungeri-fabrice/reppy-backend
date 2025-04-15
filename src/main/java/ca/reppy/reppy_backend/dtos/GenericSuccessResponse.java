package ca.reppy.reppy_backend.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericSuccessResponse {
    private String status;
    private Object data;
}
