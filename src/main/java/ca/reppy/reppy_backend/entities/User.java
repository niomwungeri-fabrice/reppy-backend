package ca.reppy.reppy_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Equipment> equipments = new ArrayList<>();
}
