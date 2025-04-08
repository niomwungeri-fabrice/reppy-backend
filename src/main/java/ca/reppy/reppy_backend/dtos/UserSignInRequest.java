package ca.reppy.reppy_backend.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSignInRequest {
    private String email;
    private String otp;
}
