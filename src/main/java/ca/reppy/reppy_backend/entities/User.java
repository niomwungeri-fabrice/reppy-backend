package ca.reppy.reppy_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;
    private String otp;
    private boolean isOtpUsed;
    private Date otpExpiry;
    private boolean emailVerified;
    private String profilePicUrl;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
}
